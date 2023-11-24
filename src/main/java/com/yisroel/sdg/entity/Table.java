package com.yisroel.sdg.entity;

import lombok.Data;

import java.util.List;
@Data
public class Table {
    String tableName;
    List<Item> list;

    @Data
    public static class Item {
        String filed;
        String type;
        String isNull;
        String key;
        String defaultValue;
        String extra;
    }
}
