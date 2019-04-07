package com.maxzxwd.easymulticutout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectImageAdapter extends RecyclerView.Adapter<ProjectImageAdapter.ViewHolder> {
    private final List<File> projectImages;
    private final SelectableTouchImageView editorView;

    public ProjectImageAdapter(File projectDir, SelectableTouchImageView editorView) {
        projectImages = new ArrayList<>(AndroidUtil.getSortedListFiles(projectDir, new ProjectFilesComparator()));
        this.editorView = editorView;
    }

    public void add(File file) {
        projectImages.add(file);
        notifyItemInserted(projectImages.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        ImageView imageView = new ImageView(viewGroup.getContext());
        imageView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final ViewHolder viewHolder = new ViewHolder(imageView) {
            @Override
            void delete() {
                if (AndroidUtil.deleteRecursive(projectImages.remove(getAdapterPosition()))) {
                    notifyItemRemoved(getAdapterPosition());
                }
            }
        };
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = projectImages.get(viewHolder.getAdapterPosition()).getAbsolutePath();
                Bitmap bitmap = AndroidUtil.loadLargeBitmap(path);
                BitmapDrawable bd = new BitmapDrawable(null, bitmap);
                bd.setAntiAlias(false);
                bd.setFilterBitmap(false);
                editorView.setImageDrawable(bd);
                editorView.setMaxZoom(bitmap.getWidth() / 10.0f);
            }
        });
        imageView.setAdjustViewBounds(true);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final String path = projectImages.get(i).getAbsolutePath();
        viewHolder.imageView.setImageBitmap(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = AndroidUtil.loadLargeBitmap(path);
                viewHolder.imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return projectImages.size();
    }

    public static abstract class ViewHolder extends DeletableViewHolder {
        final ImageView imageView;

        public ViewHolder(ImageView imageView) {
            super(imageView);

            this.imageView = imageView;
        }
    }
}
