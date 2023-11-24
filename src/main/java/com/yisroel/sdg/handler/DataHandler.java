package com.yisroel.sdg.handler;

import com.mifmif.common.regex.Generex;
import com.yisroel.sdg.entity.Type;
import com.yisroel.sdg.service.CommandService;
import com.yisroel.sdg.util.FileReadUtil;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class DataHandler {
    private static String surnameStr;
    private static String nameStr;
    @Resource
    CommandService service;
    Random random = new Random();

    @PostConstruct
    void init() {
        surnameStr = FileReadUtil.read("classpath:data-res/surname.txt");
        nameStr = FileReadUtil.read("classpath:data-res/name.txt");

    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getPersonName() {
        String surname = String.valueOf(surnameStr.charAt(random.nextInt(surnameStr.length())));
        String name1 = String.valueOf(nameStr.charAt(random.nextInt(nameStr.length())));
        String name2 = String.valueOf(nameStr.charAt(random.nextInt(nameStr.length())));
        return surname + name1 + name2;
    }

    public String getAge() {
        return String.valueOf(random.nextInt(100));
    }

    public String getSex() {
        if (random.nextBoolean()) {
            return "男";
        }
        return "女";
    }

    public String getTel() {
        StringBuilder tel = new StringBuilder("+861");
        for (int i = 0; i < 10; i++) {
            tel.append(random.nextInt(10));
        }
        return tel.toString();
    }

    public String getaddress() {
        return "";
    }

    public String getRegexString(String regex) {
        Generex generex = new Generex(regex);
        //使用正则表达式随机生成一个符合正则表达式规则的字符串
        return generex.random();
    }

    public static void main(String[] args) {
        Generex generex = new Generex("[0-9a-zA-Z]{12,13}");
        //使用正则表达式随机生成一个符合正则表达式规则的字符串
        String s = generex.random();
        System.out.println(s);
    }

    public String getData(Type type, String regex) {
        String data;
        switch (type) {
            case age:
                data = getAge();
                break;
            case sex:
                data = getSex();
                break;
            case tel:
                data = getTel();
                break;
            case address:
                data = getaddress();
                break;
            case name:
                data = getPersonName();
                break;
            case regex:
                data = getRegexString(regex);
                break;
            default:
                data = "";
        }
        return data;
    }

    public String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public String generateRandomTimeStamp() {
        return LocalDateTime.now().format(dateTimeFormatter);
    }

    public String generateRandomDate() {
        return LocalDateTime.now().format(dateFormatter);
    }
}
