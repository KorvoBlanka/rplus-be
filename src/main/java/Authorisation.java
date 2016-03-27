import morphia.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.post;

/**
 * Created by owl on 3/26/16.
 */
public class Authorisation {
    static Logger logger = LoggerFactory.getLogger(App.class);
    static final boolean AUTH_CHECK_DISABLE = false;

    public Authorisation() {
        setupEndpoints();
    }

    private void setupEndpoints() {

        before((request, response) -> {
            if (AUTH_CHECK_DISABLE) return;
            if (!request.uri().matches("/session/login")) {
                if (request.session().attribute("auth") == null ||
                        (boolean) request.session().attribute("auth") != true) {
                    halt(401, "unauthorized");
                }
            }
        });

        post("/session/login", (request, res) -> {

            String authStr64 = request.headers("Authorization").split(" ")[1];

            logger.info(authStr64);

            Base64.Decoder dec = java.util.Base64.getDecoder();
            byte[] authBytes = dec.decode(authStr64);
            String authStr = new String(authBytes, "utf-8");

            String name = authStr.split(":")[0];
            String pass = authStr.split(":")[1];

            User user = App.userService.getByName(name);
            if (user != null && user.name.matches(name) && user.password.matches(pass)) {
                request.session().attribute("auth", true);
                return "OK";
            } else {
                return "FAIL";
            }


        });

        post("/session/logout", (request, res) -> {
            request.session().attribute("auth", false);
            return "OK";
        });

    }
}
