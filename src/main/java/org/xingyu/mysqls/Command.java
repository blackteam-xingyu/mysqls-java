package org.xingyu.mysqls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static org.xingyu.mysqls.Uitl.getOptToString;
import static org.xingyu.mysqls.Uitl.sortSelectSql;

public class Command {
    public JSONObject sqlObj = new JSONObject();

    private String ArrayToString(String[] obj, String insert) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s : obj) {
            if (i > 0) {
                sb.append(insert);
            }
            sb.append(s);
            i++;
        }
        return sb.toString();
    }

    /**
     * 需要查询的table表
     *
     * @param tableOpt String 案例：table('user')
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 18:28
     */
    public Command table(String tableOpt) {
        if (tableOpt != null && tableOpt.contains("SELECT")) {
            tableOpt = "(" + tableOpt + ")";
        }
        if (tableOpt != null) {
            this.sqlObj.put("table", tableOpt);
        }
        return this;
    }

    /**
     * 增加where条件
     *
     * @param whereOpt String | JSONObject | ArrayList
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 18:45
     */
    public Command where(String whereOpt) {
        this.sqlObj.put("where", whereOpt);
        return this;
    }

    public Command where(JSONObject whereOpt) {
        String result;
        result = getOptToString(whereOpt);
        this.sqlObj.put("where", result);
        return this;
    }

    public Command where(JSONArray whereOpt) {
        String result;
        result = getOptToString(whereOpt);
        this.sqlObj.put("where", result);
        return this;
    }

    /**
     * 查询字段 案例： field('id,name,age,sex') | field(['id','name','age','sex'])
     *
     * @param fieldOpt String | String[]
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 19:01
     */
    public Command field(String fieldOpt) {
        this.sqlObj.put("field", fieldOpt);
        return this;
    }

    public Command field(String[] fieldOpt) {
        String newFieldOpt = ArrayToString(fieldOpt, ",");
        this.sqlObj.put("field", newFieldOpt);
        return this;
    }

    /**
     * 设置别名
     *
     * @param aliasOpt String 案例： alias('a')
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 19:04
     */
    public Command alias(String aliasOpt) {
        this.sqlObj.put("alias", aliasOpt);
        return this;
    }

    /**
     * 设置data数据 案例： {name:'zane',email:'752636052@qq.com'} | 'name=zane&email=752636052@qq.com'
     *
     * @param dataOpt JSONObject | string
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 19:30
     */
    public Command data(String dataOpt) {
        JSONObject newOpt = new JSONObject();
        String newDataOpt = dataOpt;
        String[] arr = newDataOpt.split("&");
        for (String s : arr) {
            String[] itemArr = s.split("=");
            newOpt.put(itemArr[0], itemArr[1]);
        }
        this.sqlObj.put("data", newOpt);
        return this;
    }

    public Command data(JSONObject dataOpt) {
        this.sqlObj.put("data", dataOpt);
        return this;
    }

    /**
     * order排序 参数为 String[] | String
     *
     * @param orderOpt
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 19:42
     */
    public Command order(String orderOpt) {
        String orderBy = "ORDER BY";
        this.sqlObj.put("order", orderBy + " " + orderOpt);
        return this;
    }

    public Command order(String[] orderOpt) {
        String orderBy = "ORDER BY";
        String newOrderOpt = "";
        newOrderOpt = ArrayToString(orderOpt, ",");
        this.sqlObj.put("order", orderBy + " " + newOrderOpt);
        return this;
    }

    /**
     * limit 语句 案例：limit(10) | limit(10,20)
     *
     * @param limitOpt int
     * @return Command
     * @author 糖糖
     * @date 2022/6/28 19:56
     */
    public Command limit(int... limitOpt) {
        if (limitOpt.length > 1) {
            this.sqlObj.put("limit", "LIMIT " + limitOpt[0] + ',' + limitOpt[1]);

        } else if (limitOpt.length == 1) {
            this.sqlObj.put("limit", "LIMIT " + limitOpt[0]);
        }
        return this;
    }

    /**
     * page 语句 案例 page(1,10) | page(2,10) | page('3,10')
     *
     * @param pageOpt int | String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 14:14
     */
    public Command page(int... pageOpt) {
        if (pageOpt.length == 2) {
            String begin = String.valueOf((pageOpt[0] - 1) * pageOpt[1]);
            String end = String.valueOf(pageOpt[1]);
            this.sqlObj.put("limit", "LIMIT " + begin + "," + end);
        }
        return this;
    }

    public Command page(String pageOpt) {
        String[] opt;
        opt = pageOpt.split(",");
        if (opt.length == 2) {
            String begin = String.valueOf((Integer.parseInt(opt[0]) - 1) * Integer.parseInt(opt[1]));
            String end = opt[1];
            this.sqlObj.put("limit", "LIMIT " + begin + "," + end);
        }
        return this;
    }

    /**
     * group 语句 案例：group('id,name')
     *
     * @param groupOpt String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 14:17
     */
    public Command group(String groupOpt) {
        this.sqlObj.put("group", "GROUP BY " + groupOpt);
        return this;
    }

    /**
     * having 语句 案例：having('count(number)>3')
     *
     * @param havingOpt String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 14:17
     */
    public Command having(String havingOpt) {
        this.sqlObj.put("having", "HAVING " + havingOpt);
        return this;
    }

    /**
     * union 语句 案例： union('SELECT name FROM node_user_1') | union(['SELECT name FROM
     * node_user_1','SELECT name FROM node_user_2'])
     *
     * @param unionOpt String | Array
     * @param type     是否为All
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 14:43
     */
    public Command union(String unionOpt, boolean type) {
        if (this.sqlObj.containsKey("union")) {
            this.sqlObj.put(
                    "union",
                    this.sqlObj.getString("union") + " (" + unionOpt + ") " + (type ? "UNION ALL" : "UNION"));
        } else {
            this.sqlObj.put("union", "(" + unionOpt + ") " + (type ? "UNION ALL" : "UNION") + " ");
        }
        return this;
    }

    public Command union(String[] unionOpt, boolean type) {
        if (this.sqlObj.containsKey("union")) {
            this.sqlObj.put(
                    "union",
                    this.sqlObj.getString("union")
                            + " ("
                            + ArrayToString(unionOpt, (type ? ") UNION ALL (" : ") UNION ("))
                            + ") "
                            + (type ? "UNION ALL" : "UNION")
                            + ' ');
        } else {
            this.sqlObj.put(
                    "union",
                    "("
                            + ArrayToString(unionOpt, (type ? ") UNION ALL (" : ") UNION ("))
                            + ") "
                            + (type ? "UNION ALL" : "UNION")
                            + ' ');
        }
        return this;
    }

    public Command union(String unionOpt) {
        if (this.sqlObj.containsKey("union")) {
            this.sqlObj.put(
                    "union",
                    this.sqlObj.getString("union") + " (" + unionOpt + ") " + "UNION");
        } else {
            this.sqlObj.put("union", "(" + unionOpt + ") " + "UNION" + " ");
        }
        return this;
    }

    public Command union(String[] unionOpt) {
        if (this.sqlObj.containsKey("union")) {
            this.sqlObj.put(
                    "union",
                    this.sqlObj.getString("union")
                            + " ("
                            + ArrayToString(unionOpt, ") UNION (")
                            + ") "
                            + "UNION"
                            + ' ');
        } else {
            this.sqlObj.put(
                    "union",
                    "("
                            + ArrayToString(unionOpt, ") UNION (")
                            + ") "
                            + "UNION"
                            + ' ');
        }
        return this;
    }

    /**
     * distinct 语句 案例：distinct(true)
     *
     * @param type boolean
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:19
     */
    public Command distinct(boolean type) {
        if (type) {
            this.sqlObj.put("distinct", "DISTINCT");
        }
        return this;
    }

    /**
     * lock 锁语法 案例：lock(true)
     *
     * @param type boolean
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:19
     */
    public Command lock(boolean type) {
        if (type) {
            this.sqlObj.put("lock", "FOR UPDATE");
        }
        return this;
    }

    /**
     * comment 为sql语句添加注释 案例：comment('查询用户的姓名')
     *
     * @param commentOpt String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:24
     */
    public Command comment(String commentOpt) {
        if (commentOpt.length() > 0) {
            this.sqlObj.put("comment", "/* " + commentOpt + " */");
        }
        return this;
    }

    /**
     * count 语句
     *
     * @param countOpt String
     * @param alias    String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:34
     */
    public Command count(String countOpt, String alias) {
        String optValue = countOpt.length() > 0 ? countOpt : "0";
        this.sqlObj.put(
                "count", "COUNT(" + optValue + ")" + (alias.length() > 0 ? " AS " + alias : ""));
        return this;
    }

    public Command count(String countOpt) {
        String optValue = countOpt.length() > 0 ? countOpt : "0";
        this.sqlObj.put("count", "COUNT(" + optValue + ")");
        return this;
    }

    /**
     * max 语句
     *
     * @param maxOpt String
     * @param alias  String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:34
     */
    public Command max(String maxOpt, String alias) {
        String optValue = maxOpt.length() > 0 ? maxOpt : "0";
        this.sqlObj.put("max", "MAX(" + optValue + ")" + (alias.length() > 0 ? " AS " + alias : ""));
        return this;
    }

    public Command max(String maxOpt) {
        String optValue = maxOpt.length() > 0 ? maxOpt : "0";
        this.sqlObj.put("max", "MAX(" + optValue + ")");
        return this;
    }

    /**
     * min 语句
     *
     * @param minOpt String
     * @param alias  String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:34
     */
    public Command min(String minOpt, String alias) {
        String optValue = minOpt.length() > 0 ? minOpt : "0";
        this.sqlObj.put("min", "MIN(" + optValue + ")" + (alias.length() > 0 ? " AS " + alias : ""));
        return this;
    }

    public Command min(String minOpt) {
        String optValue = minOpt.length() > 0 ? minOpt : "0";
        this.sqlObj.put("min", "MIN(" + optValue + ")");
        return this;
    }

    /**
     * avg 语句
     *
     * @param avgOpt String
     * @param alias  String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:34
     */
    public Command avg(String avgOpt, String alias) {
        String optValue = avgOpt.length() > 0 ? avgOpt : "0";
        this.sqlObj.put("avg", "AVG(" + optValue + ")" + (alias.length() > 0 ? " AS " + alias : ""));
        return this;
    }

    public Command avg(String avgOpt) {
        String optValue = avgOpt.length() > 0 ? avgOpt : "0";
        this.sqlObj.put("avg", "AVG(" + optValue + ")");
        return this;
    }

    /**
     * sum 语句
     *
     * @param sumOpt String
     * @param alias  String
     * @return Command
     * @author 糖糖
     * @date 2022/6/29 15:34
     */
    public Command sum(String sumOpt, String alias) {
        String optValue = sumOpt.length() > 0 ? sumOpt : "0";
        this.sqlObj.put("sum", "SUM(" + optValue + ")" + (alias.length() > 0 ? " AS " + alias : ""));
        return this;
    }

    public Command sum(String sumOpt) {
        String optValue = sumOpt.length() > 0 ? sumOpt : "0";
        this.sqlObj.put("sum", "SUM(" + optValue + ")");
        return this;
    }

    public Command join(JSONObject joinOpt) {
        String result = "";
        if (!joinOpt.containsKey("dir")
                || !joinOpt.containsKey("table")
                || !joinOpt.containsKey("where")) {
            return this;
        }
        result +=
                " "
                        + joinOpt.getString("dir").toUpperCase()
                        + " JOIN "
                        + sortSelectSql(joinOpt.getJSONObject("table"), true).getString("result")
                        + " ON "
                        + getOptToString(joinOpt.getJSONObject("where"));
        this.sqlObj.put("join", result);
        return this;
    }

    public Command join(JSONArray joinArray) {
        String result = "";
        for (int i = 0; i < joinArray.size(); i++) {
            JSONObject joinOpt = joinArray.getJSONObject(i);
            if (!joinOpt.containsKey("dir")
                    || !joinOpt.containsKey("table")
                    || !joinOpt.containsKey("where")) {
                continue;
            }
            result +=
                    " "
                            + joinOpt.getString("dir").toUpperCase()
                            + " JOIN "
                            + sortSelectSql(joinOpt.getJSONObject("table"), true).getString("result")
                            + " ON "
                            + getOptToString(joinOpt.getJSONObject("where"));
        }
        this.sqlObj.put("join", result);
        return this;
    }
}
