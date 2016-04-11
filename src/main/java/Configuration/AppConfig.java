package Configuration;

import com.google.gson.Gson;
import utils.CommonUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by owl on 3/27/16.
 */
public class AppConfig {
    public static String API_CONTEXT = "/api/v1";
    public static String SERVICE_CONTEXT = "/service/v1";

    public static String STATIC_FILE_LOCATION = "/home/owl/projects/public";

    public static String FILE_STORAGE_PATH = STATIC_FILE_LOCATION + "/file_storage/";
    public static String FILE_STORAGE_URL = "http://localhost:4567/file_storage/";
    public static String PHOTO_STORAGE_PATH = STATIC_FILE_LOCATION + "/photo_storage/";
    public static String PHOTO_STORAGE_URL = "http://localhost:4567/photo_storage/";

    public static void LoadConfig() throws IOException {
        String json_str = new String(Files.readAllBytes(Paths.get("app_config")));
        Map<String, String> conf_map = CommonUtils.JsonToMap(json_str);

        AppConfig.STATIC_FILE_LOCATION = conf_map.get("STATIC_FILE_LOCATION");

        AppConfig.FILE_STORAGE_PATH = conf_map.get("FILE_STORAGE_PATH");
        AppConfig.FILE_STORAGE_URL = conf_map.get("FILE_STORAGE_URL");
        AppConfig.PHOTO_STORAGE_PATH = conf_map.get("PHOTO_STORAGE_PATH");
        AppConfig.PHOTO_STORAGE_URL = conf_map.get("PHOTO_STORAGE_URL");
    }
}
