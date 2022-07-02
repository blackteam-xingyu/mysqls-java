package org.xingyu.mysqls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;


public class CRUD extends Command {
    CRUD() {
        super();

    }

    public String select() {
        String result = "";
        if (this.sqlObj.containsKey("union")) {
            result = this.sqlObj.getString("union");
            if (result.substring(result.length() - 10).contains("ALL")) {
                result = result.replaceAll("\\sUNION\\sALL\\s*$", "");
            } else {
                result = result.replaceAll("\\sUNION\\s*$", "");
            }
            this.sqlObj = new JSONObject();
            return result;
        }

        JSONObject newSqlObj = Uitl.sortSelectSql(this.sqlObj);
        JSONArray sortkeys = newSqlObj.getJSONArray("sortkeys");
        for (Object item : sortkeys) {
            if (newSqlObj.getJSONObject("result").containsKey((String) item)) {
                result = result + " " + newSqlObj.getJSONObject("result").getString((String) item);
            }
        }
        final String sqlStr =
                "SELECT "
                        + result.replace('\"', '\'').replace('`', '\'')
                        + " ";
        this.sqlObj = new JSONObject();
        return sqlStr;
    }

    public Object select(boolean type) {
        if (type) {
            String result = "";
            if (this.sqlObj.containsKey("union")) {
                result = this.sqlObj.getString("union");
                if (result.substring(result.length() - 10).contains("ALL")) {
                    result = result.replaceAll("\\sUNION\\sALL\\s*$", "");
                } else {
                    result = result.replaceAll("\\sUNION\\s*$", "");
                }
                this.sqlObj = new JSONObject();
                return result;
            }
            JSONObject newSqlObj = Uitl.sortSelectSql(this.sqlObj);
            String[] sortkeys = (String[]) newSqlObj.get("sortkeys");
            for (final String item : sortkeys) {
                if (newSqlObj.getJSONObject("result").containsKey(item)) {
                    result += " " + newSqlObj.getJSONObject("result").getString(item);
                }
            }
            final String sqlStr =
                    "SELECT "
                            + result.replace('\"', '\'').replace('`', '\'')
                            + " ";
            this.sqlObj.put("sqlStr", sqlStr);
            return this;
        } else {
            return this.select();
        }
    }

    public String update() {
        String result = "";
        String dataStr = "";
        JSONObject newOpt = this.sqlObj.getJSONObject("data");
        ArrayList<String> keys = new ArrayList<>(newOpt.keySet());
        for (int i = 0; i < keys.size(); i++) {
            final String item = keys.get(i);
            dataStr = i == keys.size() - 1 ?
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, false, false) :
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, false, false) + ",";

        }
        result = this.sqlObj.containsKey("where") ?
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr + " WHERE " + this.sqlObj.getString("where") :
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr;
        final String sqlStr = result.replace('\"', '\'').replace('`', '\'');
        this.sqlObj = new JSONObject();
        return sqlStr;
    }

    public Object update(boolean type) {
        String result = "";
        String dataStr = "";
        JSONObject newOpt = this.sqlObj.getJSONObject("data");
        ArrayList<String> keys = new ArrayList<>(newOpt.keySet());
        for (int i = 0; i < keys.size(); i++) {
            final String item = keys.get(i);
            dataStr = i == keys.size() - 1 ?
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, type, false) :
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, type, false) + ",";

        }
        result = this.sqlObj.containsKey("where") ?
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr + " WHERE " + this.sqlObj.getString("where") :
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr;
        final String sqlStr = result.replace('\"', '\'').replace('`', '\'');
        if (type) {
            this.sqlObj.put("sqlStr", sqlStr);
            return this;
        } else {
            this.sqlObj = new JSONObject();
            return sqlStr;
        }
    }

    public Object update(boolean type, boolean bol) {
        String result = "";
        String dataStr = "";
        JSONObject newOpt = this.sqlObj.getJSONObject("data");
        ArrayList<String> keys = new ArrayList<>(newOpt.keySet());
        for (int i = 0; i < keys.size(); i++) {
            final String item = keys.get(i);
            dataStr = i == keys.size() - 1 ?
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, type, bol) :
                    dataStr + item + "=" + Uitl.checkOptType(newOpt.get(item), item, type, bol) + ",";

        }
        result = this.sqlObj.containsKey("where") ?
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr + " WHERE " + this.sqlObj.getString("where") :
                "UPDATE " + this.sqlObj.getString("table") + " SET " + dataStr;
        final String sqlStr =
                result.replace('\"', '\'').replace('`', '\'');
        if (type && !bol) {
            this.sqlObj.put("sqlStr", sqlStr);
            return this;
        } else {
            this.sqlObj = new JSONObject();
            return sqlStr;
        }
    }

    public String insert() {
        JSONObject newOpt = this.sqlObj.getJSONObject("data");
        final String dataStr = Uitl.handleInsertData(newOpt);
        String result = "INSERT INTO " + this.sqlObj.getString("table") + " " + dataStr;
        final String sqlStr =
                result.replace('\"', '\'').replace('`', '\'');
        this.sqlObj = new JSONObject();
        return sqlStr;
    }

    public Object insert(boolean type) {
        JSONObject newOpt = this.sqlObj.getJSONObject("data");
        final String dataStr = Uitl.handleInsertData(newOpt);
        String result = "INSERT INTO " + this.sqlObj.getString("table") + " " + dataStr;
        final String sqlStr =
                result.replace('\"', '\'').replace('`', '\'');
        if (type) {
            this.sqlObj.put("sqlStr", sqlStr);
            return this;
        } else {
            this.sqlObj = new JSONObject();
            return sqlStr;
        }
    }

    public String delete() {
        String result = this.sqlObj.containsKey("where") ?
                "DELETE FROM " + this.sqlObj.getString("table") + " WHERE " + this.sqlObj.getString("where") :
                "DELETE FROM " + this.sqlObj.getString("table");
        final String sqlStr =
                result.replace('\"', '\'').replace('`', '\'');
        this.sqlObj = new JSONObject();
        return sqlStr;
    }

    public Object delete(boolean type) {
        String result = this.sqlObj.containsKey("where") ?
                "DELETE FROM " + this.sqlObj.getString("table") + " WHERE " + this.sqlObj.getString("where") :
                "DELETE FROM " + this.sqlObj.getString("table");
        final String sqlStr =
                result.replace('\"', '\'').replace('`', '\'');
        if (type) {
            this.sqlObj.put("sqlStr", sqlStr);
            return this;
        } else {
            this.sqlObj = new JSONObject();
            return sqlStr;
        }
    }

    public String query() {
        this.sqlObj = new JSONObject();
        return "";
    }

    public String query(String opt) {
        this.sqlObj = new JSONObject();
        return opt;
    }

    public Object query(String opt, boolean type) {
        if (type) {
            this.sqlObj.put("sqlStr", opt);
            return this;
        } else {
            this.sqlObj = new JSONObject();
            return opt;
        }
    }

    @Override
    public CRUD table(String tableOpt) {
        super.table(tableOpt);
        return this;
    }

    @Override
    public CRUD where(String whereOpt) {
        super.where(whereOpt);
        return this;
    }

    @Override
    public CRUD where(JSONObject whereOpt) {
        super.where(whereOpt);
        return this;
    }

    @Override
    public CRUD where(JSONArray whereOpt) {
        super.where(whereOpt);
        return this;
    }

    @Override
    public CRUD field(String fieldOpt) {
        super.field(fieldOpt);
        return this;
    }

    @Override
    public CRUD field(String[] fieldOpt) {
        super.field(fieldOpt);
        return this;
    }

    @Override
    public CRUD alias(String aliasOpt) {
        super.alias(aliasOpt);
        return this;
    }

    @Override
    public CRUD data(String dataOpt) {
        super.data(dataOpt);
        return this;
    }

    @Override
    public CRUD data(JSONObject dataOpt) {
        super.data(dataOpt);
        return this;
    }

    @Override
    public CRUD order(String orderOpt) {
        super.order(orderOpt);
        return this;
    }

    @Override
    public CRUD order(String[] orderOpt) {
        super.order(orderOpt);
        return this;
    }

    @Override
    public CRUD limit(int... limitOpt) {
        super.limit(limitOpt);
        return this;
    }

    @Override
    public CRUD page(int... pageOpt) {
        super.page(pageOpt);
        return this;
    }

    @Override
    public CRUD page(String pageOpt) {
        super.page(pageOpt);
        return this;
    }

    @Override
    public CRUD group(String groupOpt) {
        super.group(groupOpt);
        return this;
    }

    @Override
    public CRUD union(String unionOpt) {
        super.union(unionOpt);
        return this;
    }

    @Override
    public CRUD union(String[] unionOpt) {
        super.union(unionOpt);
        return this;
    }

    @Override
    public CRUD having(String havingOpt) {
        super.having(havingOpt);
        return this;
    }

    @Override
    public CRUD union(String unionOpt, boolean type) {
        super.union(unionOpt, type);
        return this;
    }

    @Override
    public CRUD union(String[] unionOpt, boolean type) {
        super.union(unionOpt, type);
        return this;
    }

    @Override
    public CRUD distinct(boolean type) {
        super.distinct(type);
        return this;
    }

    @Override
    public CRUD lock(boolean type) {
        super.lock(type);
        return this;
    }

    @Override
    public CRUD comment(String commentOpt) {
        super.comment(commentOpt);
        return this;
    }

    @Override
    public CRUD count(String countOpt, String alias) {
        super.count(countOpt, alias);
        return this;
    }

    @Override
    public CRUD count(String countOpt) {
        super.count(countOpt);
        return this;
    }

    @Override
    public CRUD max(String maxOpt, String alias) {
        super.max(maxOpt, alias);
        return this;
    }

    @Override
    public CRUD max(String maxOpt) {
        super.max(maxOpt);
        return this;
    }

    @Override
    public CRUD min(String minOpt, String alias) {
        super.min(minOpt, alias);
        return this;
    }

    @Override
    public CRUD min(String minOpt) {
        super.min(minOpt);
        return this;
    }

    @Override
    public CRUD avg(String avgOpt, String alias) {
        super.avg(avgOpt, alias);
        return this;
    }

    @Override
    public CRUD avg(String avgOpt) {
        super.avg(avgOpt);
        return this;
    }

    @Override
    public CRUD sum(String sumOpt, String alias) {
        super.sum(sumOpt, alias);
        return this;
    }

    @Override
    public CRUD sum(String sumOpt) {
        super.sum(sumOpt);
        return this;
    }

    @Override
    public CRUD join(JSONObject joinOpt) {
        super.join(joinOpt);
        return this;
    }

    @Override
    public CRUD join(JSONArray joinArray) {
        super.join(joinArray);
        return this;
    }
}
