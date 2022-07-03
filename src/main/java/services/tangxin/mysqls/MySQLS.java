package services.tangxin.mysqls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MySQLS extends CRUD {
    private boolean isTransaction = false;
    private Connection connection = null;
    private Config config = new Config();
    private Statement stmt = null;

    public MySQLS() {
        super();
        this.sqlObj = new JSONObject();
    }

    /**
     * 初始化连接，不填参数则读取配置文件。 如果不使用自带的数据库连接功能，仅仅需要语句生成功能，则不需要初始化。
     *
     * @param config Config
     * @throws SQLException SQLException
     * @author 糖糖
     */
    public void init(Config config)
            throws SQLException, IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, InstantiationException {
        boolean isPool = config.getConfig().getJSONObject("datasource").containsKey("type");
        if (isPool) {
            putPool(config.getConfig().getJSONObject("datasource").getString("type"));
        } else {
            putDriver(config.getConfig().getJSONObject("datasource").getString("driver-class-name"));
        }
    }

    /**
     * 初始化连接，不填参数则读取配置文件。 如果不使用自带的数据库连接功能，仅仅需要语句生成功能，则不需要初始化。
     *
     * @throws SQLException SQLException
     * @author 糖糖
     */
    public void init()
            throws SQLException, IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, InstantiationException {
        config = new Config(true);
        boolean isPool = config.getConfig().getJSONObject("datasource").containsKey("type");
        if (isPool) {
            putPool(config.getConfig().getJSONObject("datasource").getString("type"));
        } else {
            putDriver(config.getConfig().getJSONObject("datasource").getString("driver-class-name"));
        }
    }

    public static JSONObject Obj(String json) {
        try {
            if (json.startsWith("{") && json.endsWith("}")) {
                return JSONObject.parseObject(json);
            } else {
                throw new Error("传参不符合json格式");
            }
        } catch (Error e) {
            throw new Error("传参不符合json格式");
        }
    }

    public static JSONArray Arr(String json) {
        try {
            if (json.startsWith("[") && json.endsWith("]")) {
                return JSONObject.parseArray(json);
            } else {
                throw new Error("传参不符合json格式");
            }
        } catch (Error e) {
            throw new Error("传参不符合json格式");
        }
    }

    /**
     * 关闭连接
     *
     * @author 糖糖
     */
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * 运行返回JSONArray数据格式
     *
     * @param sqlString SQL String
     * @return JSONArray
     * @throws SQLException SQLException
     * @author 糖糖
     */
    public JSONArray exec(String sqlString) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        List<Map<String, Object>> list = extractData(resultSet);
        resultSet.close();
        statement.close();
        return JSONArray.parseArray(JSON.toJSONString(list));
    }

    public JSONArray exec() throws SQLException {
        if (this.sqlObj.containsKey("sqlStr")) {
            return exec(this.sqlObj.getString("sqlStr"));
        } else {
            throw new Error("未生成sql语句");
        }
    }

    /**
     * 运行返回通用List-Map数据格式
     *
     * @param sqlString SQL String
     * @return List
     * @author 糖糖
     */
    public List<Map<String, Object>> execListMap(String sqlString) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        List<Map<String, Object>> list = extractData(resultSet);
        resultSet.close();
        statement.close();
        return list;
    }

    /**
     * 运行返回List-Object数据格式
     *
     * @param sqlString SQL String
     * @param clazz
     * @return List
     * @throws SQLException SQLException
     * @author 糖糖
     */
    public <T> List<Object> execListObj(String sqlString, Class<T> clazz) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        List<Map<String, Object>> list = extractData(resultSet);
        List<Object> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(map);
            result.add(JSON.toJavaObject(jsonObject, clazz));
        }
        resultSet.close();
        statement.close();
        return result;
    }

    /**
     * 事务方法，没有返回值所以SELECT没有意义
     *
     * @param sqlString SQL String
     * @author 糖糖
     */
    public void transaction(String... sqlString) throws SQLException {
        try {
            // Assume a valid connection object conn
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();

            for (String sql : sqlString) {
                stmt.executeUpdate(sql);
            }
            connection.commit();
        } catch (SQLException se) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void putDriver(String driver) {
        try {
            Class.forName(driver);
            final String username = this.config.getConfig().getJSONObject("datasource").getString("username");
            final String password = this.config.getConfig().getJSONObject("datasource").getString("password");
            final String url = this.config.getConfig().getJSONObject("datasource").getString("url");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putPool(String pool)
            throws SQLException, IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, InstantiationException {
        switch (pool) {
            case "com.alibaba.druid.pool.DruidDataSource":
                druid();
                break;
            default:
                break;
        }
    }

    private void druid()
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, SQLException, InstantiationException {
        Object dataSourceObj = Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory").newInstance();
        Method dataSourceMethodsetUrl =
                Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory")
                        .getMethod("setUrl", String.class);
        dataSourceMethodsetUrl
                .invoke(dataSourceObj, config.getConfig().getJSONObject("datasource").getString("url"));
        Method dataSourceMethodsetDriverClassName =
                Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory")
                        .getMethod("setDriverClassName", String.class);
        dataSourceMethodsetDriverClassName
                .invoke(dataSourceObj, config.getConfig().getJSONObject("datasource").getString("driver-class-name"));
        Method dataSourceMethodsetUsername =
                Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory")
                        .getMethod("setUsername", String.class);
        dataSourceMethodsetUsername
                .invoke(dataSourceObj, config.getConfig().getJSONObject("datasource").getString("username"));
        Method dataSourceMethodsetPassword =
                Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory")
                        .getMethod("setPassword", String.class);
        dataSourceMethodsetPassword
                .invoke(dataSourceObj, config.getConfig().getJSONObject("datasource").getString("password"));
        Method dataSourceMethodgetConnection =
                Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory")
                        .getMethod("setPassword");
        connection = (Connection) dataSourceMethodgetConnection
                .invoke(dataSourceObj);
    }

    private List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int num = md.getColumnCount();
        List<Map<String, Object>> listOfRows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> mapOfColValues = new HashMap<>(num);
            for (int i = 1; i <= num; i++) {
                mapOfColValues.put(md.getColumnName(i), rs.getObject(i));
            }
            listOfRows.add(mapOfColValues);
        }
        return listOfRows;
    }

}
