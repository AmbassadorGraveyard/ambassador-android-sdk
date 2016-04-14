package com.ambassador.demo.exports;

public interface Export<T> {

    void setModel(T t);
    String getReadme();
    String getJavaImplementation();
    String getSwiftImplementation();
    String getObjectiveCImplementation();
    String zip();

}
