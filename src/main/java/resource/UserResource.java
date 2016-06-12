package resource; /**
 * Created by owl on 3/23/16.
 */


import configuration.AppConfig;
import com.google.gson.Gson;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import utils.JsonTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class UserResource {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserService userService;

    Gson gson = new Gson();

    public UserResource(UserService userService) {
        this.userService = userService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/user/create", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            User user = userService.create(request.body());

            result.put("response", "ok");
            result.put("result", user);
            response.status(201);

            return result;
        }, new JsonTransformer());


        post(AppConfig.API_CONTEXT + "/user/update/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            User user = userService.update(request.params(":id"), request.body());

            result.put("response", "ok");
            result.put("result", user);
            response.status(202);

            return result;
        }, new JsonTransformer());


        post(AppConfig.API_CONTEXT + "/user/delete/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            User user = userService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", user);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/user/get/:id", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();
            User user = userService.get(request.params(":id"));

            result.put("response", "ok");
            result.put("result", user);

            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/user/list", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            String role = "";
            if (request.queryParams("role") != null) {
                role = request.queryParams("role");
            }

            List<User> userList = userService.list(role, "");

            result.put("response", "ok");
            result.put("result", userList);

            return result;
        }, new JsonTransformer());

    }
}
