package com.yisroel.sdg.util;

import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileReadUtil {

    public static String read(String path) {
        StringBuilder stringBuilder=new StringBuilder();
        try {
            File file = ResourceUtils.getFile(path);
            FileReader fileReader = null;
            stringBuilder = new StringBuilder();
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        //System.out.println(read("classpath:data-res/name.txt"));
        for (int i=0;i<10;i++){

        }
    }
}
