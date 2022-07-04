# MYSQL-JAVA
mysqls-java 是一款mysql语句的生成插件。支持链式调用与直接使用json字符串生成mysql的sql语句。同时支持生成sql语句后直接调用，兼容了druid的连接池，支持事务调用。
* maven地址：https://repo1.maven.org/maven2/services/tangxin/mysqls
* 算法参考项目：https://github.com/wangweianger/mysqls (MIT)

## 安装

```xml
<dependency>
    <groupId>services.tangxin.mysqls</groupId>
    <artifactId>mysqls-java</artifactId>
    <version>1.0.2</version>
</dependency>
```

## 核心类
> * MySQLS：  MySQLS插件的核心类，一切的使用都是根据此类生成的对象。
> * Config：  MySQLS插件的配置类，用于初始化时的配置，可以被配置文件取代

## 核心方法
> * init：  初始化配置方法，如果需要用该插件直接请求数据库则需要调用。如果仅需生成sql语句，则无需调用。
> * exec：  运行方法，当插件使用init正确初始化配置后，可以使用此方法直接运行。
> * transaction：  事务处理方法，当用此插件直接调用sql语句时，可以用该方法处理事务。因为事务中的select在业务层面意义不大，所以，该事务处理方法调用成功时没有返回值。
> * Obj：  static转换方法，将String类型转化为JSONObject类型
> * Arr：  static转换方法，将String类型转化为JSONArray类型

## 插件使用

### 1、生成对象使用

```java
import services.tangxin.mysqls.MySQLS;

class TryMySQLS {
//...
    void try()

    {
        MySQLS sql = new MySQLS();
        String sqlReq = sql.table("user").select();//生成sql语句
        System.out.println("生成的sql语句为:" + sqlReq);
        //生成的sql语句为:SELECT  * FROM user 
    }
//...
}
```
### 2、在Spring/SpringBoot中整合（推荐）
* 这里只介绍纯注解开发
```java
//新建一个MySQLConfig.java或者直接写入您的配置类中
//MySQLSConfig.java
@Configuration
public class MySQLSConfig {
    @Bean
    public MySQLS MySQLS() {
        return new MySQLS();
    }
}
//UserController.java
@Slf4j
@Controller
public class UserController{
//...
	@Resource(name = "MySQLS")
    private MySQLS sql;
    public void testMysqls() {
        log.info(sql.table("user").select());
//2022-07-03 02:59:43.207 -- [main] INFO com.******.***.UserController.testMysqls - SELECT  * FROM user 
//...
    }
}
```
因为spring Bean容器的特性，只会在项目初始化的时候执行一次new。此后Bean使用的是同一个地址内的对象，所以仅仅调用一次init方法就可以完成所有的业务。避免了需要访问数据库时反复调用init方法初始化所造成的性能损耗。

## MySQLS配置初始化
> 无需使用该插件的sql语句运行功能的使用者，请直接跳过该步骤，直接用生成的sql语句去整合Mybatis。

### 1、使用Config类
```java
class TryMySQLS{
//...
	void try(){
		Config config = new Config();
		//（必填）设置sql驱动，以后或许会兼容其他数据库，目前虽然仅完美支持mysql，但是如果sql语句不冲突，直接加载其他关系型数据库的的驱动也是可以使用的。冲突的语句可以在其后直接调用query(String sql)方法硬编码sql语句解决。
		config.setDriveClassName("com.mysql.cj.jdbc.Driver");
		//（选填）配置连接池，目前仅兼容alibaba的druid连接池，需要单独安装，不久将会提供其他连接池。如果不配置就不使用连接池
		config.setType("com.alibaba.druid.pool.DruidDataSource");
		//（必填）配置数据库用户名
		config.setUsername("username");
		//（必填）配置数据库密码
		config.setPassword("password");
		//（必填）配置数据库地址
		config.setUrl("jdbc:mysql://localhost:3306/TEST?serverTimezone=UTC");
		Mysql sql = new Mysql();
		sql.init(config.config);//初始化配置
		//...
		sql.close();//使用结束后记得关闭连接，如果使用spring的Bean可以将写入Bean的生命周期销毁
	}
//...
}
```
将数据库配置硬编码在代码里是一个不合适的编程习惯，如果需要动态改变驱动之类的配置时可以斟酌使用此方法。但对外公开项目中请勿硬编码数据库用户名密码和地址信息！！！

### 2、使用配置文件方式（推荐）
> 配置文件参数说明
> driver-class-name:       数据库驱动类
> type:							   连接池（DruidDataSource）
> username:					 数据库用户名
> password:					  数据库密码
> url:				                  数据库连接地址
> 支持直接配置在springboot的application.yml或者application.properties中
```yml
#application.yml（和mybatis同款配置）
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    username: my_username
    password: my_password
    url: jdbc:mysql://localhost:3306/TEST?serverTimezone=UTC
```
``` properties
#application.properties（和mybatis同款配置）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.username=my_username
spring.datasource.password=my_password
spring.datasource.url=jdbc:mysql://localhost:3306/TEST?serverTimezone=UTC
```
>如果单独配置，请在resources文件夹下新建一个mysqls.yml或者mysql.properties
```yml
#mysqls.yml
datasource:
  driver-class-name: com.mysql.cj.jdbc.Driver
  type: com.alibaba.druid.pool.DruidDataSource
  username: my_username
  password: my_password
  url: jdbc:mysql://localhost:3306/TEST?serverTimezone=UTC
```
``` properties
#mysqls.properties
datasource.driver-class-name=com.mysql.cj.jdbc.Driver
datasource.type=com.alibaba.druid.pool.DruidDataSource
datasource.username=my_username
datasource.password=my_password
datasource.url=jdbc:mysql://localhost:3306/TEST?serverTimezone=UTC
```
* xml配置方式后续会开发支持，目前暂不支持xml配置，所以spring框架而非springboot的使用者请用mysql.yml或者mysql.properties的方式配置
> 在配置好配置文件后需要调用init方法，当然spring/springboot开发者也可以在Bean的配置类完成
```java
//MySQLSConfig.java
@Configuration
public class MySQLSConfig {
    @Bean
    public MySQLS MySQLS() {
        MySQLS mysqls = new MySQLS();
        //以下两种方式使用其一即可
        //1、手动根据文件生成配置，需要动态配置时可使用
        Config dynamic_config = new Config(true);
        //dynamic_config.setType
        mysqls.init(dynamic_config.config);
        //2、自动生成配置
        mysqls.init();
        //将初始化后的对象挂载到Bean
        return mysqls;
    }
}
```

## 典型案例
> 本插件所有与JSON有关语句转换功能都由阿里的fastjson插件实现，包里已自带插件依赖。如果不喜欢JSON语句也可以选择直接使用sql语句的字符串。详情可参考文档。
### 只生成sql语句案例
```java
//...
String json = "{'id':1}";
//链式调用生成sql语句
String sql = sql.table("user").field("id,name").where(Obj(json)).select();
log.info(sql);
/*2022-07-03 16:05:41.395 -- [main] INFO  com.spring.mytest.MytestApplicationTests.testMysqls - 
SELECT  id,name FROM user WHERE id=1*/  
//...
```

### 使用exec(String sql)函数执行sql语句
```java
//...
String json = "{'id':1}";
//调用exec执行语句返回查询结果
String sql=sql.table("user").field("id,name").where(Obj(json)).select();sql.exec(sql);
//...
```

### 使用transaction(String...sql)处理事务
```java
//...
String json = "{'id':1}";
//生成sql语句
String sql1= sql.table('table1').data(Obj("{number:'number-5'}")).update();
String sql2= sql.table('table2').data(Obj("{number:'number+5'}")).update();
//调用transaction执行事务处理（不定参数，有多少条语句就传多少个参数）
sql.transaction(sql1,sql2);
//...
```

### 生成sql语句简单用法
> sql调用方法的顺序内部已经做了排序，因此可以不按严格的sql语句顺序来写。但select、insert、update、delete、query这种CRUD开头语句的方法要放在最后写。

**查询**
```java
MySQLS sql = new MySQLS();
sql
	.table('user')
	.field('id,name')
	.where(Obj("{'id':1}"))
	.select();
//SELECT id,name FROM user WHERE id=1
```

**插入**
```java
MySQLS sql = new MySQLS();
sql
    .table('user')
    .data(Obj("{'name':'zhangsan','email':'fwkt@qq.com'}"))
    .insert();

//INSERT INTO user (name,email) VALUES (`zhangsan`,`fwkt@qq.com`)
```

**批量插入**
```java
String array = "["+
    "{'name':'zhangsan','email':'fwkt@qq.com'},"+
    "{'name':'luoxiang','email':'xfjs@qq.com'},"+
    "{'name':'houda','email':'fkwz@qq.com'}"+
"]";
sql
	.table("user")
	.data(Arr(array))
	.insert();

//INSERT INTO user (name,email) VALUES ('zhangsan','fwkt@qq.com'),('luoxiang','xfjs@qq.com'),('houda','fkwz@qq.com')	
```

**更新**
```java
sql
    .table('user')
    .data(Obj("{'name':'zhangsan','email':'fwkt@qq.com'}"))
    .where(Obj("{'id':1}"))
    .update();

//UPDATE user SET name=`zhangsan`,email=`fwkt@qq.com`WHERE id=1
```

**删除**
```java
sql .table('user')
    .where(Obj("{'name':'zhangsan'}"))
    .delet();


//
```

### 生成sql语句高级用法

**参数json多字段**
```java
sql
    .table('user')
    .where(Obj("{'id':1,'name':'zhangsan'}"))
    .select();
    
//SELECT  * FROM user WHERE id=1 AND name='zhangsan'
```

**参数json数组**
```java
String data = "["+
    "{id:1,name:'zhangsan','_type':'or'},"+
    "{'sex':1,'number':3}"+
"]";
sql.table('user').where(Arr(data)).select();
//SELECT * FROM user WHERE (id=1 OR name='zhangsan' ) AND (sex=1 AND number=3 )
```

**多字段连接方式**
```java
String data = "["+
    "{id:1,name:'zhangsan','_type':'or','_nexttype':'or'},"+
    "{'sex':1,'number':3,'_type':'and'}"+
"]";
sql.table('user').where(Arr(data)).select();
//SELECT * FROM user WHERE (id=1 OR name='zhangsan' ) OR (sex=1 AND number=3 )
```

**混合查询**
```java
String data="[{"+
    "'id':{'eq':100,'egt':10,'_type':'or'},"+
    "'name':'zhangshan',"+
    "'_nexttype':'or'"+
"},{"+
    "'status':1,"+
    "'name':{'like':'%zane%'}"+
"}]";
sql.table('user').where(Arr(data)).select();
//SELECT * FROM user WHERE (((id=100) OR (id>=10)) AND name=`zhangshan`) OR (status=1 AND ((name LIKE `%zane%`))) 
```

**UNION ， UNION ALL 组合使用**
```java
sql
    .union('SELECT * FROM think_user_1',true)
    .union('SELECT * FROM think_user_2',true)
    .union(Arr"['SELECT * FROM think_user_3','SELECT name FROM think_user_4']")
    .union('SELECT * FROM think_user_5',true)
    .select();
    
/*(SELECT * FROM think_user_1) UNION ALL  
(SELECT * FROM think_user_2) UNION ALL 
(SELECT * FROM think_user_3) UNION 
(SELECT name FROM think_user_4)  UNION  
(SELECT * FROM think_user_5)*/
```

* 更多用法请参考详细文档（未完待续）