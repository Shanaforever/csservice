package com.centricsoftware.core.service;

@FunctionalInterface
public interface UploadServiceFunctional<T,R> {
    R doSth(T t);
}
