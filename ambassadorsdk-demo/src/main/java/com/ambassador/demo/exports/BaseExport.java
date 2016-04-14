package com.ambassador.demo.exports;

import android.content.Context;

public abstract class BaseExport<T> implements Export<T> {

    protected T model;

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

    @Override
    public void addExtra(String pathToAdd, String pathToContent) {

    }

    @Override
    public String zip(Context context) {
        return new Zipper(context)
                .add("README.txt", getReadme())
                .add("MyActivity.java", getJavaImplementation())
                .add("AppDelegate.swift", getSwiftImplementation())
                .add("AppDelegate.m", getObjectiveCImplementation())
                .zip(getZipName());
    }

    @Override
    public abstract String getZipName();

}
