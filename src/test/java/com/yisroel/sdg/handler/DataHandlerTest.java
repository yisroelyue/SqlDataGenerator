package com.yisroel.sdg.handler;

import com.yisroel.sdg.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class DataHandlerTest {
    @Autowired
    private DataHandler service;

    @Test
    public void testA(){
        System.out.println("hello");
    }
}
