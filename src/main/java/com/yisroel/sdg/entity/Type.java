package com.yisroel.sdg.entity;

public enum Type {
    name,
    tel,
    sex,
    address,
    age,
    regex;
    public static Type parse(String s){
        return Type.valueOf(s);
    }

}
