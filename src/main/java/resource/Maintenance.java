package resource;

import Configuration.AppConfig;
import com.google.gson.Gson;
import morphia.entity.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OfferService;
import service.PhotoService;
import utils.CommonUtils;
import utils.JsonTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.post;

/**
 * Created by owl on 4/5/16.
 */
public class Maintenance {

    Logger logger = LoggerFactory.getLogger(Maintenance.class);

    private final OfferService offerService;
    private final PhotoService photoService;

    Gson gson = new Gson();

    public Maintenance(OfferService offerService, PhotoService photoService) {
        this.offerService = offerService;
        this.photoService = photoService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.SERVICE_CONTEXT + "/offer/put", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Offer offer = offerService.create(request.body());

            result.put("response", "ok");
            result.put("result", offer);
            response.status(201);

            return result;
        }, new JsonTransformer());

        post(AppConfig.SERVICE_CONTEXT + "/photo/put/:id", "application/json", (request, response) -> {

            Map<String, String> photo = CommonUtils.JsonToMap(request.body());
            photoService.put(request.params(":id"), photo.get("url"));
            response.status(201);
            return null;
        }, new JsonTransformer());
    }

}
