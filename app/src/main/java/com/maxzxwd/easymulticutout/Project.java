package com.maxzxwd.easymulticutout;

import android.graphics.Bitmap;

import java.io.File;

public class Project {
    public final File dir;
    public final String name;
    public final Bitmap cover;

    public Project(File dir, File coverFile) {
        this.dir = dir;
        this.name = dir.getName();
        if (coverFile.exists() && coverFile.isFile()) {
            this.cover = AndroidUtil.loadLargeBitmap(coverFile.getAbsolutePath());
        } else {
            this.cover = null;
        }
    }
}
