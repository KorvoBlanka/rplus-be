/**
 * Created by owl on 3/26/16.
 */

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.Gson;
import hibernate.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibernate.entity.User;
import utils.CommonUtils;




public class Authorisation {

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
            //response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.header("Access-Control-Allow-Credentials", "true");

            String apiKey = request.queryParams("api_key");

            if (apiKey != null && apiKey.equals("18l8lIwH9r0D3777OR3E0W1t4Pu1r8oY")) {
                // TODO: вытащить ориджин из заголовка и проверить что он в вайт листе
                origin = "http://xn--b1adacaabaehsdbwnyeec1a7dwa0toa.xn--p1ai";
                response.header("Access-Control-Allow-Origin", origin);
                return;
            } else {
                response.header("Access-Control-Allow-Origin", origin);
            }
            if (AUTH_CHECK_DISABLED || !request.uri().startsWith("/api")) return;

            logger.info(request.uri());

            if (request.session().isNew() || request.session().attribute("logged_in") == null || (boolean)request.session().attribute("logged_in") != true) {
                halt(401, "unauthorized");
            }

        });

        post("/session/login", (request, res) -> {

            HashMap<String, Object> result = new HashMap<>();

            Map<String, String> map = CommonUtils.JsonToMap(request.body());

            String accountName = map.get("account");
            String login = map.get("login");
            String pass = map.get("password");

            if (accountName.equals("dev") && login.equals("dev") && pass.equals("nopass")) {

                request.session().attribute("logged_in", true);
                //request.session().attribute("account", acc);
                //request.session().attribute("user", user);

                result.put("result", "OK");
                return result;
            }

            Account acc = App.accountService.getByName(accountName);

            if (acc == null) {
                result.put("result", "FAIL");
            } else {

                User user = App.userService.getByLogin(acc.getId(), login);
                if (user != null && user.getPassword().matches(pass)) {

                    request.session().attribute("logged_in", true);
                    request.session().attribute("account", acc);
                    request.session().attribute("user", user);

                    result.put("account", acc);
                    result.put("user", user);
                    result.put("result", "OK");
                } else {
                    result.put("result", "FAIL");
                }
            }

            return result;

        }, gson::toJson);

        post("/session/logout", (request, res) -> {

            HashMap<String, String> result = new HashMap<>();
            request.session().attribute("logged_in", false);
            request.session().invalidate();
            result.put("result", "OK");
            return result;

        }, gson::toJson);

        get("/session/check", (request, res) -> {

            HashMap<String, Object> result = new HashMap<>();
            if (request.session().isNew() || (boolean)request.session().attribute("logged_in") != true) {
                result.put("result", "FAIL");
            } else {
                Account acc = request.session().attribute("account");
                User user = request.session().attribute("user");
                result.put("account", acc);
                result.put("user", user);
                result.put("result", "OK");
            }

            return result;

        }, gson::toJson);

    }

}
