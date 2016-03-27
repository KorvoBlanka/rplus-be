package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by owl on 3/26/16.
 */
public class CommonUtils {

    public static Map<String, String> JsonToMap(String json) {
        Gson gson = new Gson();
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        Map<String,String> map = gson.fromJson(json, stringStringMap);
        return map;
    }
}
