package com.yisroel.sdg.service;

import com.yisroel.sdg.config.DBConfig;
import com.yisroel.sdg.entity.Table;
import com.yisroel.sdg.entity.Type;
import com.yisroel.sdg.handler.DBHandler;
import com.yisroel.sdg.handler.TableOutputHandler;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.util.*;

@ShellComponent
public class CommandService {
    @Resource
    DBHandler dbHandler;
    @Resource
    DBConfig dbConfig;
    @Resource
    TableOutputHandler tableOutputHandler;
    Connection connection;
    List<String> tabList;
    String curTableName;
    Table table;

    Map<Integer, Type> configMap = new HashMap<>();
    Map<Integer, String> regexConfigMap = new HashMap<>();

    boolean isTestEnabled = false;

    /**
     * 使用默认配置
     */
    @PostConstruct
    @ShellMethod(value = "初始化（使用默认配置）", key = "init")
    public String init() {
        connect(dbConfig.getDefaultHost(), dbConfig.getDefaultDb(), dbConfig.getDefaultUser(), dbConfig.getDefaultPs());
        return "user default config";
    }

    /**
     * 创建指定数据库连接
     * example: connect -h localhost -d test -u root -p 123456
     */
    @ShellMethod(value = "连接指定数据库[-h:host][-d:db][-u:user][-p:ps]", key = "connect")
    public String connect(@ShellOption(value = {"--host", "-h"}) String host,
                          @ShellOption(value = {"--db", "-d"}) String db,
                          @ShellOption(value = {"--user", "-u"}) String user,
                          @ShellOption(value = {"--ps", "-p"}) String ps) {
        connection = dbHandler.getConnect(host, db, user, ps);
        //在连接完成后填充nameList
        tabList = dbHandler.showTables(connection);
        if (!Objects.isNull(connection)) {
            return "connect success";
        } else
            return "connect failed";
    }

    /**
     * 列出表
     * example: showTables
     */
    @ShellMethod(value = "列出当前数据库所有表", key = "st")
    public String showTables() {
        StringBuilder tabStr = new StringBuilder();
        for (int i = 0; i < tabList.size(); i++) {
            tabStr.append("[").append(i).append("]").append(" -> ").append(tabList.get(i)).append("\n");
        }
        return tabStr.toString();
    }


    /**
     * 选择表  根据showTables指定索引
     * example: useTable -i 0
     */
    @ShellMethod(value = "选择表,根据showTables指定索引 [-i:索引]",
            key = "ut")
    public String useTable(@ShellOption(value = {"--index", "-i"}) int index) {
        isTestEnabled = true;
        curTableName = tabList.get(index);
        table = dbHandler.getTableStructure(connection, curTableName);
        List<List<String>> lists = new ArrayList<>();
        List<String> l = new ArrayList<>();
        l.add("field");
        l.add("type");
        l.add("null");
        l.add("key");
        l.add("default");
        l.add("extra");
        lists.add(l);
        table.getList().forEach(e -> {
            List<String> list = new ArrayList<>();
            list.add(e.getFiled());
            list.add(e.getType());
            list.add(e.getIsNull());
            list.add(e.getKey());
            list.add(e.getDefaultValue());
            list.add(e.getExtra());
            lists.add(list);
        });
        String tableOutputStr = tableOutputHandler.output(lists, 6);
        return "use table " + curTableName + "\n" + tableOutputStr;
    }

    /**
     * 创建数据-增加字段配置
     * 多次执行该命令可为多个字段设置属性
     * conf -i 1 -t personName
     * 正则：可通过增加regex类型并指定 -r[regex]参数生成指定类型数据
     */
    @ShellMethod(value = "增加配置项[-i:索引][-t:类型={name,tel,sex,age,regex}][-r:regex]", key = "conf")
    public String conf(@ShellOption(value = {"--index", "-i"}) int index,
                       @ShellOption(value = {"--type", "-t"}) String type,
                       @ShellOption(value = {"--regex", "-r"}, defaultValue = "") String regex) {
        if (!regex.isEmpty()) {
            regexConfigMap.put(index, regex);
        }
        try {
            configMap.put(index, Type.parse(type));
        } catch (Exception e) {
            return "type is wrong";
        }
        return "新增配置项成功";
    }

    /**
     * 列出配置项
     * example: showConfig
     */
    @ShellMethod(value = "列出配置项", key = "sf")
    public String showConfig() {
        List<List<String>> lists = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add("INDEX");
        titleList.add("TYPE");
        lists.add(titleList);
        configMap.forEach((k, v) -> {
            List<String> l = new ArrayList<>();
            l.add(String.valueOf(k));
            l.add(String.valueOf(v));
            lists.add(l);
        });
        return tableOutputHandler.output(lists, 2);
    }

    /**
     * 生成数据
     *
     * @return
     */
    @ShellMethod(value = "生成测试数据[-s:数量]", key = "gen")
    public String generate(@ShellOption(value = {"--size", "-s"}) int size) {
        long startTime = System.currentTimeMillis();
        boolean b = dbHandler.genData(connection, table, configMap, regexConfigMap, size);
        if (!b) {
            return "生成数据错误";
        }
        long finish = System.currentTimeMillis();
        long runTime = finish - startTime;
        return "生成数据完成，共耗时" + runTime + "ms";
    }


    /**
     * 关闭连接
     */
    @ShellMethod(value = "断开数据库连接", key = "dc")
    public String disconnect() {
        try {
            tabList.clear();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "disconnect over";
    }

    public Availability generateAvailability() {
        return isTestEnabled ? Availability.available() : Availability.unavailable("请先执行ut命令");
    }
}
