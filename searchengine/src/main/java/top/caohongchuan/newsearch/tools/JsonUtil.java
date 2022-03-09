package top.caohongchuan.newsearch.tools;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonUtil {

    /**
     * 读取JSON文件转换为字符串
     * @param filePath
     * @return
     */
    public static String readJsonFile(String filePath) {
        String jsonStr = "";
        try {
            Reader reader = new InputStreamReader(new ClassPathResource(filePath).getInputStream());
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}

