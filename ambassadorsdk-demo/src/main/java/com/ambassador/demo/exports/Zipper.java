package com.ambassador.demo.exports;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles custom packaging in the demo app for sharing integrations. Class can be used to add files
 * at a given path + filename and then zip it. Has special logic for stuff like RAFOptions and ConversionParameters.
 */
public class Zipper {

    /** A context reference passed into the class constructor used for saving and reading files properly. */
    protected Context context;

    /** List of String path+file that need to be packaged into the zip as the base directory. */
    protected List<String> files;

    /**
     * Default constructor.
     * @param context a reference to a valid Context.
     */
    public Zipper(@NonNull Context context) {
        this.context = context;
        this.files = new ArrayList<>();
    }

     /**
     * Adds a plaintext file to the package with a passed in path + name and the String content. Saves
     * it and will later be packaged into the zip.
     * @param pathWithName the String relative path inside assets with the filename on the end.
     * @param content the String content to write in the file.
     * @return this Zipper, useful for chaining methods.
     */
    @NonNull
    public Zipper add(@NonNull String pathWithName, @NonNull String content) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(pathWithName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        files.add(pathWithName);

        return this;
    }

    /**
     * Adds a file to the package with a passed location from app internal storage.
     * @param path the String relative path inside internal storage.
     * @return this Zipper, useful for chaining methods.
     */
    @NonNull
    public Zipper add(@NonNull String path) {
        files.add(path);
        return this;
    }

    /**
     * Zips all the added files and returns the path.
     * @return the String path + filename of the zip file.
     */
    @NonNull
    public String zip(String filename) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[1024];

            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = context.openFileInput(files.get(i));
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
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

        return filename;
    }

}
