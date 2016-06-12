package resource;

import configuration.AppConfig;
import com.google.gson.Gson;
import entity.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OrganisationService;
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
public class OrganisationResource {

    Logger logger = LoggerFactory.getLogger(OrganisationResource.class);

    private final OrganisationService orgService;

    Gson gson = new Gson();

    public OrganisationResource(OrganisationService orgService) {
        this.orgService = orgService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/organisation/create", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Organisation user = orgService.create(request.body());

            result.put("response", "ok");
            result.put("result", user);
            response.status(201);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/organisation/update/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Organisation org = orgService.update(request.params(":id"), request.body());

            result.put("response", "ok");
            result.put("result", org);
            response.status(202);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/organisation/delete/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Organisation org = orgService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", org);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/organisation/get/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Organisation org = orgService.get(request.params(":id"));

            result.put("response", "ok");
            result.put("result", org);

            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/organisation/list", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String searchQuery = "";

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("search_query") != null) {
                searchQuery = request.queryParams("search_query");
            }

            List<Organisation> orgList = orgService.list(page, perPage, searchQuery);

            result.put("response", "ok");
            result.put("result", orgList);

            return result;
        }, new JsonTransformer());

    }

}
