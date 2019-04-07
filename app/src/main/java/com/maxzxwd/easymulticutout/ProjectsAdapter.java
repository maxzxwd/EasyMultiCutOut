package com.maxzxwd.easymulticutout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Project> projectList = new ArrayList<>();

    public void add(Project project) {
        projectList.add(project);
        notifyItemInserted(projectList.size() - 1);
    }

    public ProjectsAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.project_card, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view) {
            @Override
            void delete() {
                if (AndroidUtil.deleteRecursive(projectList.remove(getAdapterPosition()).dir)) {
                    notifyItemRemoved(getAdapterPosition());
                }
            }
        };
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra("ProjectPath", projectList.get(viewHolder.getAdapterPosition()).dir);
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final File dirForZip = projectList.get(viewHolder.getAdapterPosition()).dir;
                final File zipFile = new File(context.getExternalCacheDir(), dirForZip.getName() + ".zip");
                final ProgressDialog prepairingDialog = ProgressDialog.show(context, "",
                        context.getResources().getString(R.string.preparing_for_share), true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtil.zip(dirForZip.listFiles(), zipFile.getAbsolutePath());
                        prepairingDialog.dismiss();

                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("*/*");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                                context, BuildConfig.APPLICATION_ID, zipFile));
                        context.startActivity(Intent.createChooser(emailIntent,
                                context.getResources().getString(R.string.share)));
                    }
                }).start();

                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Project project = projectList.get(i);

        viewHolder.nameTextView.setText(project.name);
        viewHolder.coverImageView.setImageBitmap(project.cover);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static abstract class ViewHolder extends DeletableViewHolder {
        final TextView nameTextView;
        final ImageView coverImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.name_text);
            this.coverImageView = itemView.findViewById(R.id.cover_image);
        }
    }
}
