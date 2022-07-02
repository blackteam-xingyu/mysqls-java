package test.org.xingyu.mysqls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xingyu.mysqls.MySQLS;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author 糖糖
 * 
 */

public class TestTest {

    @Before
    public void before() throws Exception {
        System.out.println("======================开始测试======================");
    }

    @After
    public void after() throws Exception {
        System.out.println("======================结束测试======================");
    }

    @Test
    public void test() throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        MySQLS sql = new MySQLS();
        JSONArray field = JSONArray.parseArray(
                "[{\"id\":1,\"name\":\"zhangsan\",\"_type\":\"or\"},{\"sex\":1,\"number\":3}]"
        );
        JSONObject data = JSONObject.parseObject("{\n" +
                "    name:'zhangsan',\n" +
                "    age:25\n" +
                "}");
        sql.init();
        System.out.println(

                sql.exec(sql.table("user")
//                        .data(data)
//                                .field(new String[]{"id AS Id", "name AS Name"})
//                                .order(new String[]{"id desc", "user"})
//                        .where(field)
//                        .group("user_id")
//                        .page(3, 10)
//                        .distinct(true)
                                .comment("查询node_table表的所有数据")
                                .select()
//                        .delete()
                )
        );
    }
} 
