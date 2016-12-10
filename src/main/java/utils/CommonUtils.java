package utils;
/**
 * Created by owl on 3/26/16.
 */

import configuration.AppConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Instant;
import java.util.Map;


public class CommonUtils {

    public static Map<String, String> JsonToMap(String json) {
        Gson gson = new Gson();
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(json, stringStringMap);
        return map;
    }

    public static long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String downloadPhoto(String photoUrl) throws Exception {
        BufferedImage image = null;

        URL url = new URL(photoUrl);
        image = ImageIO.read(url);

        Instant now = Instant.now();
        File outputfile = new File(AppConfig.PHOTO_STORAGE_PATH + Long.toString(now.toEpochMilli()) + ".png");
        ImageIO.write(image, "png", outputfile);

        return outputfile.getName();
    }
}
