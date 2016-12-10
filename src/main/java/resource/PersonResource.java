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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.AppConfig;
import service.PersonService;
import hibernate.entity.Person;


public class PersonResource {

    Logger logger = LoggerFactory.getLogger(PersonResource.class);
    Gson gson = new Gson();

    private final PersonService personService;


    public PersonResource(PersonService personService) {

        this.personService = personService;
        setupEndpoints();

    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/person/list", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            Integer userId = null;
            Integer organisationId = null;
            String searchQuery = null;

            String pageStr = request.queryParams("page");
            if (pageStr != null && StringUtils.isNumeric(pageStr)) {
                page = Integer.parseInt(pageStr);
            }

            String perPageStr = request.queryParams("perPage");
            if (perPageStr != null && StringUtils.isNumeric(perPageStr)) {
                perPage = Integer.parseInt(perPageStr);
            }

            String userIdStr = request.queryParams("userId");
            if (userIdStr != null && StringUtils.isNumeric(userIdStr)) {
                userId = Integer.parseInt(userIdStr);
            }

            String orgStr = request.queryParams("organisationId");
            if (orgStr != null && StringUtils.isNumeric(orgStr)) {
                organisationId = Integer.parseInt(orgStr);
            }

            if (request.queryParams("searchQuery") != null) {
                searchQuery = request.queryParams("searchQuery");
            }

            List<Person> personList = personService.list(page, perPage, userId, organisationId, searchQuery);

            result.put("response", "ok");
            result.put("result", personList);

            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/person/get/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String personIdStr = request.params(":id");

            if (personIdStr != null && StringUtils.isNumeric(personIdStr)) {
                long id = Long.parseLong(personIdStr);
                Person person = personService.get(id);

                result.put("response", "ok");
                result.put("result", person);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }


            return result;
        }, gson::toJson);

        post(AppConfig.API_CONTEXT + "/person/save", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            Person person = gson.fromJson(request.body(), Person.class);
            List<String> errors = personService.check(person);

            if (errors.size() == 0) {
                Person res = personService.save(person);

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


        post(AppConfig.API_CONTEXT + "/person/delete/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String personIdStr = request.params(":id");

            if (personIdStr != null && StringUtils.isNumeric(personIdStr)) {
                long id = Long.parseLong(personIdStr);
                Person person = personService.delete(id);

                result.put("response", "ok");
                result.put("result", person);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }

            return result;
        }, gson::toJson);

    }

}
