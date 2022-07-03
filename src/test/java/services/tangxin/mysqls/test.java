package services.tangxin.mysqls;
import org.junit.Test;
import services.tangxin.mysqls.MySQLS;
import static services.tangxin.mysqls.MySQLS.Obj;

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
        System.out.println(sql.table("user").field("id,name").where(Obj(json)).select());

    }
}