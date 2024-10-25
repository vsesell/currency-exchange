package com.serge.mapper;

public interface Mapper<F,T>{
    T mapFrom(F object);
}
