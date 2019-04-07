package com.maxzxwd.easymulticutout;

import java.io.File;
import java.util.Comparator;

public class ProjectFilesComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        boolean file1IsList = false, file2IsList = false;
        if (file1.getName().startsWith("list")) {
            file1IsList = true;
        }
        if (file2.getName().startsWith("list")) {
            file2IsList = true;
        }

        long comp1, comp2;

        if (file1IsList && !file2IsList) {
            return -1;
        } else if (!file1IsList && file2IsList) {
            return 1;
        } else if (!file1IsList && !file1IsList) {
            comp1 = file1.lastModified();
            comp2 = file2.lastModified();
        } else {
            comp1 = Integer.parseInt(file1.getName().substring(4));
            comp2 = Integer.parseInt(file2.getName().substring(4));
        }

        return (comp1 < comp2) ? -1 : ((comp1 == comp2) ? 0 : 1);
    }
}
