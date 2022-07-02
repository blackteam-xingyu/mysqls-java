package org.xingyu.mysqls;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Getter
@ToString
@NoArgsConstructor
public class Config {
    private JSONObject config = new JSONObject();

    Config(boolean type) {
        if (type) {
            try (InputStream content =
                         this.getClass().getClassLoader().getResourceAsStream("mysqls.properties");) {
                Properties prop = new Properties();
                prop.load(content);
                Map<String, Object> map = new HashMap<>();
                Enumeration mapEenumeration = prop.propertyNames();
                while (mapEenumeration.hasMoreElements()) {
                    String key = (String) mapEenumeration.nextElement();
                    String value = prop.getProperty(key);
                    map.put(key, value);
                }
                this.config = new JSONObject(map);
                assert content != null;
                content.close();
                return;
            } catch (Exception ignored) {
            }
            try (InputStream content =
                         this.getClass().getClassLoader().getResourceAsStream("application.properties");) {
                Properties prop = new Properties();
                prop.load(content);
                Map<String, Object> map = new HashMap<>();
                Enumeration mapEenumeration = prop.propertyNames();
                while (mapEenumeration.hasMoreElements()) {
                    String key = (String) mapEenumeration.nextElement();
                    String value = prop.getProperty(key);
                    map.put(key, value);
                }
                JSONObject spring = new JSONObject(map);
                this.config = spring.getJSONObject("spring");
                assert content != null;
                content.close();
                return;
            } catch (Exception ignored) {
            }

            try (InputStream content =
                         this.getClass().getClassLoader().getResourceAsStream("mysqls.yml");) {
                Yaml yaml = new Yaml();
                Map<String, Object> it = yaml.load(content);
                this.config = new JSONObject(it);
                assert content != null;
                content.close();
                return;
            } catch (Exception ignored) {
            }

            try (InputStream content =
                         this.getClass().getClassLoader().getResourceAsStream("application.yml");) {
                Yaml yaml = new Yaml();
                Map<String, Object> it = yaml.load(content);
                JSONObject spring = new JSONObject(it);
                this.config = spring.getJSONObject("spring");
                assert content != null;
                content.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 支持链式调用 案例： MySQLS mySqls = (new
     * Config()).setDriveClassName(xxx).setType(xxx).setUsername(xxx).setPassword(xxx).setUrl(xxx).init();
     *
     * @param driveClassName String
     * @return MySQLS
     * @author 糖糖
     */

    public Config setDriveClassName(String driveClassName) {
        this.config.put("drive-class-name", driveClassName);
        return this;
    }

    public Config setType(String type) {
        this.config.put("type", type);
        return this;
    }

    public Config setUsername(String username) {
        this.config.put("username", username);
        return this;
    }

    public Config setPassword(String password) {
        this.config.put("password", password);
        return this;
    }

    public Config setUrl(String url) {
        this.config.put("url", url);
        return this;
    }
}
