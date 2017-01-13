package resource;
/**
 * Created by owl on 3/27/16.
 */

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;
import org.elasticsearch.common.geo.GeoPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.AppConfig;
import service.OfferService;
import hibernate.entity.Offer;
import utils.CommonUtils;


public class OfferResource {

    Logger logger = LoggerFactory.getLogger(OfferResource.class);
    Gson gson = new Gson();

    private final OfferService offerService;


    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/offer/list", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String search_query = "";
            Map<String, String> filters = new HashMap<>();
            GeoPoint[] polygon = new GeoPoint[0];

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("search_query") != null) {
                search_query = request.queryParams("search_query");
            }
            if (request.queryParams("filter") != null) {
                String filterStr = request.queryParams("filter");
                filters = CommonUtils.JsonToMap(filterStr);
            }

            if (request.queryParams("search_area") != null) {
                String polygonStr = request.queryParams("search_area");
                polygon = gson.fromJson(polygonStr, GeoPoint[].class);
            }


            List<Offer> offerList = offerService.list(page, perPage, filters, search_query, Arrays.asList(polygon));

            result.put("response", "ok");
            result.put("result", offerList);

            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/offer/get/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();
            long id = Long.parseLong(request.params(":id"));
            Offer offer = offerService.get(id);

            result.put("response", "ok");
            result.put("result", offer);

            return result;
        }, gson::toJson);

        post(AppConfig.API_CONTEXT + "/offer/save", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            Offer offer = gson.fromJson(request.body(), Offer.class);
            Offer res = offerService.save(offer);

            result.put("response", "ok");
            result.put("result", res);
            response.status(202);

            return result;
        }, gson::toJson);


        post(AppConfig.API_CONTEXT + "/offer/delete/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();
            int id = Integer.parseInt(request.params(":id"));
            Offer offer = offerService.delete(id);

            result.put("response", "ok");
            result.put("result", offer);

            return result;
        }, gson::toJson);

    }

}
