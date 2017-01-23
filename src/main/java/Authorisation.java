/**
 * Created by owl on 3/26/16.
 */

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.post;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibernate.entity.User;
import utils.CommonUtils;




public class Authorisation {

    static String cookieName = "RSessionId";

    static Logger logger = LoggerFactory.getLogger(Authorisation.class);

    Gson gson = new Gson();
    static final boolean AUTH_CHECK_DISABLED = false;

    public Authorisation() {
        setupEndpoints();
    }

    private void setupEndpoints() {

        before((request, response) -> {

            String origin = "http://localhost:3000";
            String methods = "*";
            String headers = "*";
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.header("Access-Control-Allow-Credentials", "true");

            if (AUTH_CHECK_DISABLED || !request.uri().startsWith("/api")) return;

            logger.info(request.uri());

            if (request.session().isNew() || (boolean)request.session().attribute("logged_in") != true) {
                halt(401, "unauthorized");
            }

        });

        post("/session/login", (request, res) -> {

            HashMap<String, String> result = new HashMap<>();

            Map<String, String> map = CommonUtils.JsonToMap(request.body());

            String account = map.get("account");
            String login = map.get("userName");
            String pass = map.get("password");

            User user = App.userService.getByLogin(account, login);
            if (user != null && user.getPassword().matches(pass)) {

                request.session().attribute("logged_in", true);

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
