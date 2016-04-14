package com.ambassador.demo.exports;

import android.content.Context;

public interface Export<T> {

    void setModel(T t);
    String getReadme();
    String getJavaImplementation();
    String getSwiftImplementation();
    String getObjectiveCImplementation();
    void addExtraContent(String filename, String content);
    void addExtraFile(String filename);
    String zip(Context context);
    String getZipName();

}
