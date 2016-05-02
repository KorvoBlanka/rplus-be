package resource;

import Configuration.AppConfig;
import com.google.gson.Gson;
import morphia.entity.Offer;
import morphia.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OfferService;
import service.UserService;
import utils.JsonTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * Created by owl on 3/27/16.
 */
public class OfferResource {

    Logger logger = LoggerFactory.getLogger(OfferResource.class);

    private final OfferService offerService;

    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/offer/create", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Offer offer = offerService.create(request.body());

            result.put("response", "ok");
            result.put("result", offer);
            response.status(201);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/offer/update/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Offer offer = offerService.update(request.params(":id"), request.body());

            result.put("response", "ok");
            result.put("result", offer);
            response.status(202);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/offer/delete/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Offer offer = offerService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", offer);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/offer/get/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Offer offer = offerService.get(request.params(":id"));

            result.put("response", "ok");
            result.put("result", offer);

            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/offer/list", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String search_query = "";
            Map<String, Integer> filters = new HashMap<String, Integer>();

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("search_query") != null) {
                search_query = request.queryParams("search_query");
            }

            List<Offer> offerList = offerService.list(page, perPage, filters, search_query);

            result.put("response", "ok");
            result.put("result", offerList);
            /*for(Offer o: result) {
                o.photo_thumbnail = "http://localhost:4567/photo_storage/" + o.photo_thumbnail;
            }*/
            return result;
        }, new JsonTransformer());

    }

}
