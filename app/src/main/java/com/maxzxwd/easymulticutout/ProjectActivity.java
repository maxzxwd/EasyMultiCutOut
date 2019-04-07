package com.maxzxwd.easymulticutout;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final File projectDir = (File) getIntent().getSerializableExtra("ProjectPath");

        RecyclerView imageRecycler = findViewById(R.id.project_image_grid);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        imageRecycler.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        new ItemTouchHelper(new SwipeToDeleteCallback()).attachToRecyclerView(imageRecycler);

        SelectableTouchImageView selectedImage = findViewById(R.id.selected_image);
        Button switchButton = findViewById(R.id.switch_button);
        Button cutButton = findViewById(R.id.cut_button);
        final SelectorManager selectorManager = new SelectorManager(selectedImage);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorManager.switchPoints();
            }
        });
        selectedImage.setChangeDrawableListener(new SelectableTouchImageView.ChangeDrawableListener() {
            @Override
            public Drawable onChangeDrawable(Drawable drawable) {
                selectorManager.resetSelections();
                return drawable;
            }
        });
        selectedImage.setLongPressListener(new SelectableTouchImageView.LongPressListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
                selectorManager.select(event.getX(), event.getY());
            }
        });
        final ProjectImageAdapter imageAdapter = new ProjectImageAdapter(projectDir, selectedImage);
        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = selectorManager.cutSelected();
                if (bitmap != null) {
                    try {
                        File newFile = new File(projectDir, bitmap.hashCode() + ".png");
                        FileOutputStream out = new FileOutputStream(newFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        imageAdapter.add(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        imageRecycler.setAdapter(imageAdapter);

        setOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setOrientation(newConfig.orientation);
    }

    private void setOrientation(int orientation) {
        LinearLayout tilesLayout = findViewById(R.id.tiles_layout);
        LinearLayout selectedImageLayout = findViewById(R.id.selected_image_layout);
        LinearLayout actionButtonsLayout = findViewById(R.id.action_buttons_layout);
        Button switchButton = findViewById(R.id.switch_button);
        Button cutButton = findViewById(R.id.cut_button);

        tilesLayout.setOrientation(orientation);
        selectedImageLayout.setOrientation(orientation);
        int oldOrientation = actionButtonsLayout.getOrientation();
        actionButtonsLayout.setOrientation((orientation + 1) % 2);

        if (actionButtonsLayout.getOrientation() != oldOrientation) {
            int width = actionButtonsLayout.getLayoutParams().width;
            int height = actionButtonsLayout.getLayoutParams().height;
            actionButtonsLayout.getLayoutParams().height = width;
            actionButtonsLayout.getLayoutParams().width = height;
            switchButton.setText(AndroidUtil.changeTextOrientation(switchButton.getText()));
            cutButton.setText(AndroidUtil.changeTextOrientation(cutButton.getText()));
        }
    }

}
