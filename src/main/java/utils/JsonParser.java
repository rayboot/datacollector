package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * @fileName: JsonParser
 * @author: h1
 * @date: 2018/4/24 13:28
 * @dscription: 解析json数据
 */
public class JsonParser {

    public static List<Map<String, String>> jsonStringToList(String jsonStr) throws Exception {
        JSONArray arr;
        try {
            arr = new JSONArray(jsonStr);
        } catch (JSONException e) {
            jsonStr = "[" + jsonStr + "]";
            arr = new JSONArray(jsonStr);
        }
        List<Map<String, String>> rsList = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonObject = arr.getJSONObject(i);
            Map<String, String> map = new HashMap<>(16);
            for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                String value = jsonObject.get(key).toString();
                map.put(key, value);
            }
            rsList.add(map);
        }
        return rsList;
    }
}
