package com.maxzxwd.easymulticutout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_PICTURES = 1;
    private final Deque<String> creatingProjects = new ArrayDeque<>();
    private File projectsDir;
    private ProjectsAdapter projectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        projectsAdapter = new ProjectsAdapter(this);

        projectsDir = getExternalFilesDir(null);
        projectsDir.mkdir();
        new Thread(new Runnable() {
            @Override
            public void run() {
            for (File dir : projectsDir.listFiles()) {
                if (dir.isDirectory()) {
                    File cover = AndroidUtil.getSortedListFiles(dir, new ProjectFilesComparator()).get(0);
                    final Project project = new Project(dir, cover);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            projectsAdapter.add(project);
                        }
                    });
                }
            }
            }
        }).start();

        RecyclerView projectsRecycler = findViewById(R.id.projects_grid);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        projectsRecycler.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        new ItemTouchHelper(new SwipeToDeleteCallback()).attachToRecyclerView(projectsRecycler);
        projectsRecycler.setAdapter(projectsAdapter);

        updateProjects(getResources().getDisplayMetrics().widthPixels);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.create_new_project_name));

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(getResources().getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        creatingProjects.push(input.getText().toString());
                        startActivityForResult(
                                Intent.createChooser(intent,
                                getResources().getString(R.string.create_new_select_image)),
                                SELECT_PICTURES);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != SELECT_PICTURES || resultCode != Activity.RESULT_OK) {
            return;
        }

        List<Uri> pictureUris = new ArrayList<>();
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                pictureUris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if(data.getData() != null) {
            pictureUris.add(data.getData());
        }

        File projectDir = new File(projectsDir, creatingProjects.pop());
        if (pictureUris.size() > 0 && projectDir.mkdir()) {
            for (int i = 0; i < pictureUris.size(); i++) {
                try {
                    AndroidUtil.copy(getContentResolver().openInputStream(pictureUris.get(i)),
                            new File(projectDir, "list" + i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            projectsAdapter.add(new Project(projectDir, new File(projectDir, "list0")));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateProjects(getResources().getDisplayMetrics().widthPixels);
    }

    private void updateProjects(int screenWidth) {
        RecyclerView projectsRecycler = findViewById(R.id.projects_grid);

        int column = screenWidth / (getResources().getDimensionPixelSize(R.dimen.fab_margin) + getResources().getDimensionPixelSize(R.dimen.project_card_size));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, column);
        projectsRecycler.setLayoutManager(mLayoutManager);
    }
}