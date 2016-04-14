package com.ambassador.demo.exports;

public interface Export<T> {

    void setModel(T t);
    String zip();

}
