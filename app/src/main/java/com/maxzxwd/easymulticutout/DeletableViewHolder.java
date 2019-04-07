package com.maxzxwd.easymulticutout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class DeletableViewHolder extends RecyclerView.ViewHolder {

    public DeletableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void delete();
}
