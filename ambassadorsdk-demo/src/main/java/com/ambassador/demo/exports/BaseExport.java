package com.ambassador.demo.exports;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseExport<T> implements Export<T> {

    protected T model;
    protected HashMap<String, String> extraContent;
    protected List<String> extraFiles;

    public BaseExport() {
        this.extraContent = new HashMap<>();
        this.extraFiles = new ArrayList<>();
    }

    @Override
    public void setModel(T t) {
        this.model = t;
    }

    @Override
    public abstract String getReadme();

    @Override
    public abstract String getJavaImplementation();

    @Override
    public abstract String getSwiftImplementation();

    @Override
    public abstract String getObjectiveCImplementation();

    public void addExtraContent(String filename, String content) {
        this.extraContent.put(filename, content);
    }

    public void addExtraFile(String filename) {
        this.extraFiles.add(filename);
    }

    @Override
    public String zip(Context context) {
        Zipper zipper =  new Zipper(context)
                .add("README.txt", getReadme())
                .add(javaClassName() + ".java", getJavaImplementation())
                .add(iOSClassName() + ".swift", getSwiftImplementation())
                .add(iOSClassName() + ".m", getObjectiveCImplementation());

        for (String key : extraContent.keySet()) {
            zipper.add(key, extraContent.get(key));
        }

        for (String file : extraFiles) {
            zipper.add(file);
        }

        return zipper.zip(getZipName());
    }

    @Override
    public abstract String getZipName();

    @Override
    public abstract String javaClassName();

    @Override
    public abstract String iOSClassName();

    protected static class PlaintextFile {

        protected String text;

        public PlaintextFile() {
            this.text = "";
        }

        public void add(String text) {
            this.text += text;
        }

        public void addLine(String text) {
            this.text += (this.text.isEmpty() ? "" : "\n") + text;
        }

        public void addHtmlLine(String text) {
            this.text += (this.text.isEmpty() ? "" : "<br/>") + text;
        }

        public void addLineWithPadding(int spaces, String text) {
            addLine("");
            for (int i = 0; i < spaces; i++) {
                add(" ");
            }

            add(text);
        }

        public String get() {
            return text;
        }

    }

}
