package com.maxzxwd.easymulticutout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class AndroidUtil {
    private AndroidUtil() {
    }

    public static void copy(InputStream srcIn, File dst) throws IOException {
        if (dst.createNewFile()) {
            try {
                OutputStream out = new FileOutputStream(dst);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = srcIn.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    out.close();
                }
            } finally {
                srcIn.close();
            }
        }
    }

    public static boolean deleteRecursive(File path) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!deleteRecursive(file))
                    return false;
            }
        }

        return path.delete();
    }

    public static int inverseColor(int color) {
        return Color.rgb(255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color));
    }

    public static CharSequence changeTextOrientation(CharSequence str) {
        if (str == null || str.length() <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length() * 2 - 1);
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) == '\n') {
                sb.deleteCharAt(sb.length() - 1);
            } else {
                sb.append(str.charAt(i)).append('\n');
            }
        }
        sb.append(str.charAt(str.length() - 1));
        sb.trimToSize();
        return sb;
    }

    public static void zip(File[] files, String zipFileName) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[1024];

            for (File file : files) {
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> getSortedListFiles(File file, Comparator<? super File> comparator) {
        List<File> result = Arrays.asList(file.listFiles());
        Collections.sort(result, comparator);
        return result;
    }

    public static Bitmap loadLargeBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;

        options.inSampleSize = Math.max(options.outWidth, options.outHeight) / 2000 + 1;
        options.inMutable = true;
        return BitmapFactory.decodeFile(path, options);
    }
}
