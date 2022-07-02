package services.tangxin.mysqls;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @ClassName: test
 * @Description:
 * @author: 糖糖
 * @date: 2022/7/3 4:24
 */
public class test {
    @Test
    public void mysql() {
        MySQLS sql = new MySQLS();
        String json = "{'id':1,'name':'user'}";
        System.out.println(sql.table("user").field("id,name").where(JSONObject.parseObject(json)).select());

    }
}