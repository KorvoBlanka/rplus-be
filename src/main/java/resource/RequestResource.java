package resource;

import configuration.AppConfig;
import com.google.gson.Gson;
import entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.RequestService;
import utils.JsonTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * Created by owl on 5/3/16.
 */
public class RequestResource {
    Logger logger = LoggerFactory.getLogger(RequestResource.class);

    private final RequestService requestService;

    Gson gson = new Gson();

    public RequestResource(RequestService requestService) {
        this.requestService = requestService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/request/create", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Request _request = requestService.create(request.body());

            result.put("response", "ok");
            result.put("result", _request);
            response.status(201);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/request/update/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();
            Request _request = requestService.update(request.params(":id"), request.body());

            result.put("response", "ok");
            result.put("result", _request);
            response.status(202);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/request/delete/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Request _request = requestService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", _request);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/request/get/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Request _request = requestService.get(request.params(":id"));

            result.put("response", "ok");
            result.put("result", _request);

            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/request/list", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String personId = "";
            String searchQuery = "";

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("person_id") != null) {
                personId = request.queryParams("person_id");
            }
            if (request.queryParams("search_query") != null) {
                searchQuery = request.queryParams("search_query");
            }

            List<Request> requestList = requestService.list(page, perPage, personId, searchQuery);

            result.put("response", "ok");
            result.put("result", requestList);

            return result;
        }, new JsonTransformer());
    }
}
