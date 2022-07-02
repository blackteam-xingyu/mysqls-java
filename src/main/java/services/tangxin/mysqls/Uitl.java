package services.tangxin.mysqls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Uitl {
    private boolean bool = false;

    public static String getOptToString(JSONObject opt) {
        String result = "";
        String type =
                opt.containsKey("_type") && opt.getString("_type").length() > 0
                        ? " " + opt.getString("_type").toUpperCase()
                        : "AND ";
        int number = opt.containsKey("_type") && opt.getString("_type").trim().length() > 0 ? 1 : 0;
        ArrayList<String> keys = new ArrayList<>(opt.keySet());
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            if (Objects.equals(key, "_type")) {
                continue;
            }
            if (opt.get(key) instanceof JSONObject) {
                if (i == keys.size() - 1 - number) {
                    result += checkOptObjTypeObj(key, (JSONObject) opt.get(key));
                } else {
                    result += checkOptObjTypeObj(key, (JSONObject) opt.get(key)) + " " + type;
                }
            } else {
                if (i == keys.size() - 1 - number) {
                    result += key + "=" + checkOptObjTypeObj(key, opt.get(key));
                } else {
                    result += key + "=" + checkOptObjTypeObj(key, opt.get(key)) + " " + type;
                }
            }
        }
        return result;
    }

    public static String getOptToString(JSONArray opt) {
        String result = "";
        for (int i = 0; i < opt.size(); i++) {
            final JSONObject item = opt.getJSONObject(i);
            String result1 = "";
            int number = 0;
            String type =
                    item.containsKey("_type") && item.getString("_type").length() > 0
                            ? item.getString("_type").toUpperCase()
                            : "AND";
            String nextType =
                    item.containsKey("_nexttype") && item.getString("_nexttype").length() > 0
                            ? item.getString("_nexttype").toUpperCase()
                            : "AND";
            number =
                    item.containsKey("_type") && item.getString("_type").trim().length() > 0
                            ? number + 1
                            : number;
            number =
                    item.containsKey("_nexttype") && item.getString("_nexttype").trim().length() > 0
                            ? number + 1
                            : number;
            ArrayList<String> keys = new ArrayList<>(item.keySet());
            for (String key : keys) {
                if (Objects.equals(key, "_type") || Objects.equals(key, "_nexttype")) {
                    continue;
                }
                if (result1.length() > 0) {
                    if (item.get(key) instanceof JSONObject) {
                        result1 += type + " " + checkOptObjType(key, item.get(key));
                    } else {
                        result1 += type + " " + key + "=" + checkOptType(item.get(key)) + " ";
                    }
                } else {
                    if (item.get(key) instanceof JSONObject) {
                        result1 += checkOptObjType(key, item.get(key));
                    } else {
                        result1 += key + "=" + checkOptType(item.get(key)) + " ";
                    }
                }
            }
            if (i == opt.size() - 1) {
                result1 = "(" + result1 + ")";
            } else {
                result1 = "(" + result1 + ") " + nextType.toUpperCase();
            }

            result = result + " " + result1;
        }
        return result;
    }

    private static String checkOptType(Object opt) {
        String result = "";
        if (opt instanceof String) {
            result = "\"" + escapeSqlSpecialChar((String) opt) + "\"";
        } else if (opt instanceof Boolean || opt instanceof Number) {
            result = String.valueOf(opt);
        } else {
            result = "\"" + escapeSqlSpecialChar((String) opt) + "\"";
        }
        return result;
    }

    public static String checkOptType(Object opt, String key, boolean type, boolean bol) {
        String result = "";
        if (opt instanceof String) {
            String newOpt = escapeSqlSpecialChar((String) opt);
            result =
                    type && bol && newOpt.contains(key) && newOpt.matches("(.*)(\\+|-|\\*|/|%)(.*)")
                            ? newOpt.substring(1, newOpt.length() - 1)
                            : newOpt;
        } else if (opt instanceof Boolean || opt instanceof Number) {
            result = String.valueOf(opt);
        } else {
            result = escapeSqlSpecialChar((String) opt);
        }
        return result;
    }
    public static String checkOptObjTypeObj(String pre_key, Object val) {
        System.out.println(val.getClass());
        String result = "";
        if (val instanceof JSONObject) {
            JSONObject newVal = (JSONObject) val;
            ArrayList<String> keys = new ArrayList<>(newVal.keySet());
            int number =
                    newVal.containsKey("_type") && newVal.getString("_type").trim().length() > 0 ? 1 : 0;
            for (int i = 0; i < keys.size(); i++) {
                final String key = keys.get(i);
                if (key == "_type") {
                    continue;
                }
                String type =
                        newVal.containsKey("_type") && newVal.getString("_type").length() > 0
                                ? newVal.getString("_type")
                                : "AND ";
                result +=
                        expressionQuery(
                                pre_key,
                                key,
                                newVal.getString(key),
                                type.toUpperCase(),
                                i == keys.size() - 1 - number);
            }
        } else if (val instanceof String) {
            result = (String) val;
        } else {
            result = String.valueOf(val);
        }
        return result;
    }
    public static String checkOptObjType(String pre_key, Object val) {
        System.out.println(val.getClass());
        String result = "";
        if (val instanceof JSONObject) {
            JSONObject newVal = (JSONObject) val;
            ArrayList<String> keys = new ArrayList<>(newVal.keySet());
            int number =
                    newVal.containsKey("_type") && newVal.getString("_type").trim().length() > 0 ? 1 : 0;
            for (int i = 0; i < keys.size(); i++) {
                final String key = keys.get(i);
                if (key == "_type") {
                    continue;
                }
                String type =
                        newVal.containsKey("_type") && newVal.getString("_type").length() > 0
                                ? newVal.getString("_type")
                                : "AND ";
                result +=
                        expressionQuery(
                                pre_key,
                                key,
                                newVal.getString(key),
                                type.toUpperCase(),
                                i == keys.size() - 1 - number);
            }
        } else if (val instanceof String) {
            result = (String) val;
        } else {
            result = String.valueOf(val);
        }
        return "(" + result + ") ";
    }

    private static String escapeSqlSpecialChar(String str) {
        return str.trim()
                .replaceAll("\\s", "")
                .replace("\\", "\\\\\\\\")
                .replace("_", "\\_")
                .replace("\'", "\\'")
                .replace("%", "\\%")
                .replace("*", "\\*");
    }

    private static String expressionQuery(
            String par_key, String chi_key, String value, String _type, boolean isLastOne) {
        String result = "";
        switch (chi_key) {
            case "EQ":
                result = "(" + par_key + "=" + checkOptType(value) + ")";
                break;
            case "NEQ":
                result = "(" + par_key + "<>" + checkOptType(value) + ")";
                break;
            case "GT":
                result = "(" + par_key + ">" + checkOptType(value) + ")";
                break;
            case "EGT":
                result = "(" + par_key + ">=" + checkOptType(value) + ")";
                break;
            case "LT":
                result = "(" + par_key + "<" + checkOptType(value) + ")";
                break;
            case "ELT":
                result = "(" + par_key + "<=" + checkOptType(value) + ")";
                break;
            case "LIKE":
                result = "(" + par_key + " LIKE " + checkOptType(value) + ")";
                break;
            case "NOTLIKE":
                result = "(" + par_key + " NOT LIKE " + checkOptType(value) + ")";
                break;
            case "BETWEEN":
                result = "(" + par_key + " BETWEEN " + value.replace(",", " AND ") + ")";
                break;
            case "NOTBETWEEN":
                result = "(" + par_key + " NOT BETWEEN " + value.replace(",", " AND ") + ")";
                break;
            case "IN":
                result = "(" + par_key + " IN " + value + ")";
                break;
            case "NOTIN":
                result = "(" + par_key + " NOT IN " + value + ")";
                break;
            default:
                result = "(" + par_key + "=" + checkOptType(value) + ")";
        }
        return isLastOne ? result + ' ' : result + ' ' + _type + ' ';
    }

    public static JSONObject sortSelectSql(JSONObject json, boolean bool) {
        JSONObject result = json;
        if (!bool) {
            if (result.containsKey("count")
                    || result.containsKey("max")
                    || result.containsKey("min")
                    || result.containsKey("avg")
                    || result.containsKey("sum")) {
                String count =
                        result.containsKey("count") && result.getString("count").length() > 0
                                ? result.getString("count")
                                : "";
                String max =
                        result.containsKey("max") && result.getString("max").length() > 0
                                ? result.getString("max")
                                : "";
                String min =
                        result.containsKey("min") && result.getString("min").length() > 0
                                ? result.getString("min")
                                : "";
                String avg =
                        result.containsKey("avg") && result.getString("avg").length() > 0
                                ? result.getString("avg")
                                : "";
                String sum =
                        result.containsKey("sum") && result.getString("sum").length() > 0
                                ? result.getString("sum")
                                : "";
                String concatstr = count + max + min + avg + sum;
                if (result.containsKey("field") && result.getString("field").length() > 0) {
                    result.put("field", result.getString("field") + concatstr);
                } else {
                    result.put("field", concatstr.substring(1));
                }

            }
            if (!result.containsKey("field") || result.getString("field").length() <= 0) {
                result.put("field", "*");
            }
            if (result.containsKey("table") && result.getString("table").length() > 0) {
                result.put("table", "FROM " + result.getString("table"));
            }
            if (result.containsKey("where") && result.getString("table").length() > 0) {
                result.put("where", "WHERE " + result.getString("where"));
            }
        } else {
            if (result.containsKey("table")) {
                result.put("table", String.valueOf(result.get("table")));
            }
        }
        ArrayList<String> keysresult = new ArrayList<>(result.keySet());
        return getJsonObject(result, keysresult);
    }

    public static JSONObject sortSelectSql(JSONObject json) {
        if (json.containsKey("count")
                || json.containsKey("max")
                || json.containsKey("min")
                || json.containsKey("avg")
                || json.containsKey("sum")) {
            String count =
                    json.containsKey("count") && json.getString("count").length() > 0
                            ? json.getString("count")
                            : "";
            String max =
                    json.containsKey("max") && json.getString("max").length() > 0
                            ? json.getString("max")
                            : "";
            String min =
                    json.containsKey("min") && json.getString("min").length() > 0
                            ? json.getString("min")
                            : "";
            String avg =
                    json.containsKey("avg") && json.getString("avg").length() > 0
                            ? json.getString("avg")
                            : "";
            String sum =
                    json.containsKey("sum") && json.getString("sum").length() > 0
                            ? json.getString("sum")
                            : "";
            String concatstr = count + max + min + avg + sum;
            if (json.containsKey("field") && json.getString("field").length() > 0) {
                json.put("field", json.getString("field") + concatstr);
            } else {
                json.put("field", concatstr.substring(1));
            }

        }
        if (!json.containsKey("field") || json.getString("field").length() <= 0) {
            json.put("field", "*");
        }
        if (json.containsKey("table") && json.getString("table").length() > 0) {
            json.put("table", "FROM " + json.getString("table"));
        }
        if (json.containsKey("where") && json.getString("table").length() > 0) {
            json.put("where", "WHERE " + json.getString("where"));
        }
        ArrayList<String> keys = new ArrayList<>(json.keySet());
        ArrayList<String> keysresult = new ArrayList<>(keys);
        return getJsonObject(json, keysresult);
    }

    private static JSONObject getJsonObject(JSONObject json, ArrayList<String> keysresult) {
        String[] searchSort = {
                "union",
                "distinct",
                "field",
                "count",
                "max",
                "min",
                "avg",
                "sum",
                "table",
                "alias",
                "join",
                "where",
                "group",
                "having",
                "order",
                "limit",
                "page",
                "comment"
        };
        final List<String> searchSortList = Arrays.asList(searchSort);
        for (int i = 0; i < keysresult.size() - 1; i++) {
            final String item = keysresult.get(i);
            boolean flag = true;
            for (int j = 0; j < keysresult.size() - i - 1; j++) {
                final String jtem = keysresult.get(j);
                final String jtem1 = keysresult.get(j + 1);
                if (searchSortList.indexOf(jtem) > searchSortList.indexOf(jtem1)) {
                    keysresult.set(j + 1, jtem);
                    keysresult.set(j, jtem1);
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        JSONObject myResult = new JSONObject();
        myResult.put("sortkeys", new JSONArray(keysresult));
        myResult.put("result", json);
        return myResult;
    }

    private static JSONArray sortArray(JSONArray data) {
        final JSONArray result = new JSONArray();
        final ArrayList<String> keys = new ArrayList<>(data.getJSONObject(0).keySet());
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < keys.size(); j++) {
                ArrayList<String> newKeys = new ArrayList<>(data.getJSONObject(i).keySet());
                if (newKeys.contains(keys.get(j)) == false) {
                    keys.remove(j);
                }
            }
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject json = new JSONObject();
            for (int j = 0; j < keys.size(); j++) {
                json.put(keys.get(j), data.getJSONObject(i).get(keys.get(j)));
            }
            result.add(json);
        }
        return result;
    }

    public static String handleInsertData() {
        return "";
    }

    public static String handleInsertData(JSONObject data) {
        String keys = "";
        String values = "";
        String datastr = "";
        ArrayList<String> objKeys = new ArrayList<>(data.keySet());
        for (int i = 0; i < objKeys.size(); i++) {
            final String key = objKeys.get(i);
            keys = keys.length() > 0 ? keys + "," + key : key;
            values =
                    values.length() > 0
                            ? values + "," + checkOptType(data.get(key))
                            : checkOptType(data.get(key));
        }
        values = "(" + values + ")";
        datastr = "(" + keys + ") VALUES " + values;
        return datastr;
    }

    public static String handleInsertData(JSONArray data) {
        Object newData;
        if (data.size() == 1) {
            newData = data.getJSONArray(0);
        } else {
            newData = data;
        }
        String keys = "";
        String values = "";
        String datastr = "";
        if (newData instanceof JSONArray) {
            JSONArray newDataArray = (JSONArray) newData;
            newDataArray = sortArray(newDataArray);
            final ArrayList<String> data0Keys = new ArrayList<>((newDataArray.getJSONObject(0)).keySet());
            keys = String.join(",", data0Keys.toArray(new String[data0Keys.size()]));
            keys = removeCharAt(keys, 0);
            keys = removeCharAt(keys, keys.length() - 1);
            for (int i = 0; i < newDataArray.size(); i++) {
                String items = "";
                JSONObject json = newDataArray.getJSONObject(i);
                ArrayList<String> jsonKeys = new ArrayList<>(json.keySet());
                for (int j = 0; j < jsonKeys.size(); j++) {
                    final String key = jsonKeys.get(j);
                    items =
                            items.length() > 0
                                    ? items + "," + checkOptType(json.get(key))
                                    : checkOptType(json.get(key));
                }
                values += "(" + items + "),";
            }
            values = removeCharAt(values, values.length() - 1);
        } else {
            JSONObject newDataObj = (JSONObject) newData;
            ArrayList<String> objKeys = new ArrayList<>(newDataObj.keySet());
            for (int i = 0; i < objKeys.size(); i++) {
                final String key = objKeys.get(i);
                keys = keys.length() > 0 ? keys + "," + key : key;
                values =
                        values.length() > 0
                                ? values + "," + checkOptType(newDataObj.get(key))
                                : checkOptType(newDataObj.get(key));
            }
            values = "(" + values + ")";
        }
        datastr = "(" + keys + ") VALUES " + values;
        return datastr;
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
