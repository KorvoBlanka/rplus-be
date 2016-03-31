import morphia.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JsonTransformer;

import java.util.Base64;
import java.util.HashMap;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.post;

/**
 * Created by owl on 3/26/16.
 */
public class Authorisation {
    static Logger logger = LoggerFactory.getLogger(App.class);
    static final boolean AUTH_CHECK_DISABLE = true;

    public Authorisation() {
        setupEndpoints();
    }

    private void setupEndpoints() {

        before((request, response) -> {
            logger.info(request.uri());

            String origin = "*";
            String methods = "*";
            String headers = "*";
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);

            if (AUTH_CHECK_DISABLE) return;
            if (request.uri().startsWith("/api")) {
                if (request.session().attribute("auth") == null ||
                        (boolean) request.session().attribute("auth") != true) {
                    halt(401, "unauthorized");
                }
            }
        });

        post("/session/login", (request, res) -> {
            HashMap<String, String> result = new HashMap<>();

            String authStr64 = request.body().split(" ")[1];
            Base64.Decoder dec = java.util.Base64.getDecoder();
            byte[] authBytes = dec.decode(authStr64);
            String authStr = new String(authBytes, "utf-8");

            String name = authStr.split(":")[0];
            String pass = authStr.split(":")[1];

            User user = App.userService.getByName(name);
            if (user != null && user.name.matches(name) && user.password.matches(pass)) {
                request.session().attribute("auth", true);
                result.put("result", "OK");
            } else {
                result.put("result", "FAIL");
            }

            return result;
        }, new JsonTransformer());

        post("/session/logout", (request, res) -> {
            HashMap<String, String> result = new HashMap<>();
            request.session().attribute("auth", false);
            result.put("result", "OK");
            return result;
        }, new JsonTransformer());

    }
}
