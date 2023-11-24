package com.yisroel.sdg.handler;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表格格式输出日志
 */
@Component
public class TableOutputHandler {
    private final int cellSize = 16;

    public String output(List<List<String>> list,int width) {
        List<String> l=new ArrayList<>();
        for (int i=0;i<width;i++){
            l.add("----------------");
        }
        list.add(0,l);
        list.add(2,l);
        list.add(list.size(),l);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                String str = list.get(i).get(j);
                int spaceSize;
                if (Objects.isNull(str)){
                    spaceSize=8;
                    str="";
                }else {
                    spaceSize= (cellSize - str.length()) / 2;
                }
                String space = genSpace(spaceSize);
                //字符长度为偶数则空格加一
                if (str.length()%2!=0){
                    stringBuilder.append(" ");
                }
                stringBuilder.append(space).append(str).append(space).append("|");

            }
            stringBuilder.append("\n");
            stringBuilder.append("|");
        }
        stringBuilder.insert(0,"|");
        stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
        return stringBuilder.toString();
    }

    private String genSpace(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}

