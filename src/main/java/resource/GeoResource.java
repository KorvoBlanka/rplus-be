package resource;

import com.google.gson.Gson;
import configuration.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.GeoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;

/**
 * Created by Aleksandr on 11.01.17.
 */
public class GeoResource {
    Logger logger = LoggerFactory.getLogger(OfferResource.class);
    Gson gson = new Gson();

    private final GeoService geoService;


    public GeoResource(GeoService geoService) {
        this.geoService = geoService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/geo/code", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String address = request.queryParams("address");
            Double[] c = this.geoService.getLocation(address);

            if (c != null) {
                result.put("response", "ok");
                result.put("result", c);
            } else {
                result.put("response", "fail");
            }


            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/geo/district", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            Double lat = Double.parseDouble(request.queryParams("lat"));
            Double lon = Double.parseDouble(request.queryParams("lon"));
            List<String> dList = this.geoService.getDistrict(lat, lon);

            if (dList != null) {
                result.put("response", "ok");
                result.put("result", dList);
            } else {
                result.put("response", "fail");
            }


            return result;
        }, gson::toJson);
    }
}
