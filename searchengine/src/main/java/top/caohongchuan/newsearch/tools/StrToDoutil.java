package top.caohongchuan.newsearch.tools;

import java.util.HashMap;
import java.util.Map;

public class StrToDoutil {

    public static HashMap<String, Double> strToDouble(HashMap<String, String> origin){
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, String> entry : origin.entrySet()) {
            result.put(entry.getKey(), Double.parseDouble(entry.getValue()));
        }
        return result;
    }
}
