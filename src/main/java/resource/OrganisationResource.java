package resource;
/**
 * Created by owl on 5/3/16.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;
import hibernate.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.AppConfig;
import service.OrganisationService;
import hibernate.entity.Organisation;


public class OrganisationResource {

    Logger logger = LoggerFactory.getLogger(OrganisationResource.class);
    Gson gson = new Gson();

    private final OrganisationService orgService;


    public OrganisationResource(OrganisationService orgService) {

        this.orgService = orgService;
        setupEndpoints();

    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/organisation/list", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String searchQuery = null;

            String pageStr = request.queryParams("page");
            if (pageStr != null && StringUtils.isNumeric(pageStr)) {
                page = Integer.parseInt(pageStr);
            }

            String perPageStr = request.queryParams("perPage");
            if (perPageStr != null && StringUtils.isNumeric(perPageStr)) {
                perPage = Integer.parseInt(perPageStr);
            }

            if (request.queryParams("searchQuery") != null) {
                searchQuery = request.queryParams("searchQuery");
            }

            List<Organisation> orgList = orgService.list(page, perPage, searchQuery);

            result.put("response", "ok");
            result.put("result", orgList);

            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/organisation/get/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String orgIdStr = request.params(":id");
            if (orgIdStr != null && StringUtils.isNumeric(orgIdStr)) {

                long id = Long.parseLong(orgIdStr);
                Organisation org = orgService.get(id);

                result.put("response", "ok");
                result.put("result", org);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }

            return result;
        }, gson::toJson);


        post(AppConfig.API_CONTEXT + "/organisation/save", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            Organisation organisation = gson.fromJson(request.body(), Organisation.class);

            List<String> errors = orgService.check(organisation);
            if (errors.size() == 0) {
                Organisation res = orgService.save(organisation);

                result.put("response", "ok");
                result.put("result", res);
                response.status(201);
            } else {
                result.put("response", "fail");
                result.put("result", errors);
                // should be 400, but its problematic to process it later so fck it
                response.status(200);
            }

            return result;
        }, gson::toJson);


        post(AppConfig.API_CONTEXT + "/organisation/delete/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String orgIdStr = request.params(":id");
            if (orgIdStr != null && StringUtils.isNumeric(orgIdStr)) {

                long id = Long.parseLong(orgIdStr);

                Organisation org = orgService.delete(id);

                result.put("response", "ok");
                result.put("result", org);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }

            return result;
        }, gson::toJson);

    }

}
