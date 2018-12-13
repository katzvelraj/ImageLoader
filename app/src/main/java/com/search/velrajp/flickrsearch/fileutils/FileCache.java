package com.search.velrajp.flickrsearch.fileutils;

import android.content.Context;

import java.io.File;

public class FileCache {
    private File imageCacheDir;

    /**
     * create cache dir in your application context
     * Creating an internal dir;
     */
    public FileCache(Context context) {
        imageCacheDir = context.getDir("vel", Context.MODE_PRIVATE);
        if (!imageCacheDir.exists()) {
            imageCacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(imageCacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = imageCacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }


}
