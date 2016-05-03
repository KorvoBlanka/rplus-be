package resource;

import Configuration.AppConfig;
import com.google.gson.Gson;
import entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OrganisationService;
import service.PersonService;
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
public class PersonResource {
    Logger logger = LoggerFactory.getLogger(OrganisationResource.class);

    private final PersonService personService;

    Gson gson = new Gson();

    public PersonResource(PersonService personService) {
        this.personService = personService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/person/create", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Person person = personService.create(request.body());

            result.put("response", "ok");
            result.put("result", person);
            response.status(201);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/person/update/:id", "application/json", (request, response) -> {

            logger.info("!!!");

            Map<String, Object> result = new HashMap<>();
            Person person = personService.update(request.params(":id"), request.body());

            result.put("response", "ok");
            result.put("result", person);
            response.status(202);

            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/person/delete/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Person person = personService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", person);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/person/get/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            Person person = personService.get(request.params(":id"));

            result.put("response", "ok");
            result.put("result", person);

            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/person/list", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String organisationId = "";
            String searchQuery = "";

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("organisation_id") != null) {
                organisationId = request.queryParams("organisation_id");
            }
            if (request.queryParams("search_query") != null) {
                searchQuery = request.queryParams("search_query");
            }

            List<Person> personList = personService.list(page, perPage, organisationId, searchQuery);

            result.put("response", "ok");
            result.put("result", personList);

            return result;
        }, new JsonTransformer());
    }
}
