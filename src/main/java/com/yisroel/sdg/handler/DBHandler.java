package com.yisroel.sdg.handler;

import com.mysql.cj.jdbc.Driver;
import com.yisroel.sdg.entity.Table;
import com.yisroel.sdg.entity.Type;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;

@Component
public class DBHandler {


    private static final String prefix = "jdbc:mysql://";

    @Resource
    DataHandler dataHandler;

    /**
     * 创建数据库连接
     */
    public Connection getConnect(String host, String db, String user, String password) {
        if (Objects.isNull(host) || Objects.isNull(db) || Objects.isNull(user) || Objects.isNull(password)) {
            return null;
        }
        String suffix = "?useUnicode=true&characterEncoding=utf8";
        String url = prefix + host + "/" + db + suffix;
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        Connection connection = null;
        try {
            Driver driver = new Driver();
            connection = driver.connect(url, properties);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return connection;
    }

    /**
     * show tables
     */
    public List<String> showTables(Connection conn) {
        List<String> list = new ArrayList<>();
        ResultSet tables = null;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            tables = metaData.getTables(
                    conn.getCatalog(),
                    null,
                    "%",
                    new String[]{"TABLE", "VIEW"});
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                list.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取表结构
     */
    public Table getTableStructure(Connection connection, String name) {
        Table table = new Table();
        List<Table.Item> list = new ArrayList<>();
        String sql = "DESCRIBE " + name;
        ResultSet resultSet = executive(connection, sql);
        try {
            while (resultSet.next()) {
                Table.Item item = new Table.Item();
                item.setFiled(resultSet.getString("Field"));
                item.setType(resultSet.getString("Type"));
                item.setIsNull(resultSet.getString("Null"));
                item.setKey(resultSet.getString("Key"));
                item.setDefaultValue(resultSet.getString("Default"));
                item.setExtra(resultSet.getString("Extra"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setList(list);
        table.setTableName(name);
        return table;
    }

    /**
     * 执行语句
     */
    public ResultSet executive(Connection connection, String sql) {
        ResultSet resultSet = null;
        try {
            Statement state = connection.createStatement();
            connection.prepareCall(sql);
            resultSet = state.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * 创建数据
     */
    public boolean genData(Connection connection, Table table, Map<Integer, Type> config, Map<Integer, String> regexConfig, int size) {

        //删除自增主键列
        int incrementIdx = -1;
        List<Table.Item> list = table.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getExtra().contains("auto_increment")) {
                incrementIdx = i;
                break;
            }
        }
        //创建sql语句
        StringBuilder sqlPrefix = new StringBuilder();
        sqlPrefix.append("INSERT INTO ").append(table.getTableName()).append("(");
        for (int i = 0; i < list.size(); i++) {
            if (incrementIdx != -1) {
                if (i == incrementIdx) {
                    continue;
                }
            }
            sqlPrefix.append(list.get(i).getFiled()).append(",");
        }
        sqlPrefix.replace(sqlPrefix.length() - 1, sqlPrefix.length(), ")");
        sqlPrefix.append(" VALUES (");
        for (int i = 0; i < list.size(); i++) {
            if (incrementIdx != -1) {
                if (i == incrementIdx) {
                    continue;
                }
            }
            sqlPrefix.append("?").append(",");
        }
        sqlPrefix.replace(sqlPrefix.length() - 1, sqlPrefix.length(), ")");

        try (PreparedStatement ps = connection.prepareStatement(sqlPrefix.toString())) {
            Random random = new Random();
            List<String> dataList = new ArrayList<>();
            //创建数据
            for (int i = 0; i < size; i++) {
                dataList.clear();
                for (int j = 0; j < list.size(); j++) {
                    //判断是否有配置项
                    String data = "";
                    if (!Objects.isNull(config.get(j))) {
                        String regex = null;
                        if (!Objects.isNull(regexConfig.get(j)) && config.get(j) == Type.regex) {
                            regex = regexConfig.get(j);
                        }
                        data = dataHandler.getData(config.get(j), regex);
                    } else {
                        //没有配置则根据类型随机生成数据
                        String type = list.get(j).getType();
                        if (type.contains("varchar") || type.contains("text")) {
                            data = dataHandler.generateRandomString(8);
                        } else if (type.contains("int") || type.contains("float") || type.contains("double")) {
                            data = String.valueOf(random.nextInt());
                        } else if (type.contains("timestamp")) {
                            data = dataHandler.generateRandomTimeStamp();
                        } else if (type.contains("date")) {
                            data = dataHandler.generateRandomDate();
                        }
                    }
                    dataList.add(data);
                }
                if (incrementIdx == -1) {
                    for (int m = 0; m < dataList.size(); m++) {
                        ps.setObject(m + 1, dataList.get(m));
                    }
                } else {
                    for (int m = 0; m < dataList.size(); m++) {
                        if (m == dataList.size() - 1) break;
                        ps.setObject(m + 1, dataList.get(m + 1));
                    }
                }
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        Table table = new Table();
        List<Table.Item> l = new ArrayList<>();
        int n = 0;
        while (n < 5) {
            Table.Item item = new Table.Item();
            item.setFiled("ID");
            item.setType("VARCHAR(16)");
            l.add(item);
            n++;
        }
        l.get(0).setExtra("auto_increment");
        table.setList(l);

    }


}
