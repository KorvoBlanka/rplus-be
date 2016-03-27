package resource; /**
 * Created by owl on 3/23/16.
 */


import Configuration.AppConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.WriteResult;
import morphia.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import spark.ResponseTransformer;
import utils.JsonTransformer;

import java.io.IOException;
import java.util.List;

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
            User result = userService.create(request.body());
            response.status(201);
            return result;
        }, new JsonTransformer());

        put(AppConfig.API_CONTEXT + "/user/update/:id", "application/json", (request, response) -> {
            User result = userService.update(request.params(":id"), request.body());
            response.status(202);
            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/user/delete/:id", "application/json", (request, response) -> {
            User result = userService.delete(request.params(":id"));
            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/user/get/:id", "application/json", (request, response) -> {
            User result = userService.get(request.params(":id"));
            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/user/list", "application/json", (request, response) -> {
            List<User> result = userService.list(request.body());
            return result;
        }, new JsonTransformer());

    }
}
