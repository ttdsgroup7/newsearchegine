package top.caohongchuan.newsrecommand.algorithms;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月30日 用以读取配置文件，获取对应属性
 */
@Slf4j
public class PropGetKit {

	public static Properties propGetKit = new Properties();

    public static void loadProperties(String configFileName) {
        try {
            propGetKit.load(new FileInputStream(System.getProperty("user.dir") + "/resources/" + configFileName + ".properties"));
        } catch (FileNotFoundException e) {
            log.error("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
        } catch (IOException e) {
            log.error("装载文件--->失败!");
        }
    }

    public static String getString(String key) {
        return propGetKit.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.valueOf(propGetKit.getProperty(key));
    }

}
