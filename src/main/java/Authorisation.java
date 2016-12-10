/**
 * Created by owl on 3/26/16.
 */

import java.util.Base64;
import java.util.HashMap;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.post;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibernate.entity.User;


public class Authorisation {

    static Logger logger = LoggerFactory.getLogger(Authorisation.class);
    Gson gson = new Gson();
    static final boolean AUTH_CHECK_DISABLED = true;

    public Authorisation() {
        setupEndpoints();
    }

    private void setupEndpoints() {

        before((request, response) -> {

            String origin = "*";
            String methods = "*";
            String headers = "*";
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);

            if (AUTH_CHECK_DISABLED || !request.uri().startsWith("/api")) return;

            logger.info(request.uri());

            if (request.session().attribute("auth") == null ||
                    (boolean) request.session().attribute("auth") != true) {
                halt(401, "unauthorized");
            }

        });

        post("/session/login", (request, res) -> {

            HashMap<String, String> result = new HashMap<>();

            String authStr64 = request.body().split(" ")[1];
            Base64.Decoder dec = java.util.Base64.getDecoder();
            byte[] authBytes = dec.decode(authStr64);
            String authStr = new String(authBytes, "utf-8");

            String a[] = authStr.split(":");
            String login = a[0];
            String pass = a[1];

            User user = App.userService.getByLogin(login);
            if (user != null && user.getPassword().matches(pass)) {
                request.session().attribute("auth", true);
                result.put("result", "OK");
            } else {
                result.put("result", "FAIL");
            }

            return result;

        }, gson::toJson);

        post("/session/logout", (request, res) -> {

            HashMap<String, String> result = new HashMap<>();
            request.session().attribute("auth", false);
            result.put("result", "OK");
            return result;

        }, gson::toJson);

    }

}
