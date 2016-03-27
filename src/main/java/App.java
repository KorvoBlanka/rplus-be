/**
 * Created by owl on 3/23/16.
 */

import com.mongodb.*;
import morphia.entity.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.UserResource;
import service.UserService;

import static spark.Spark.*;


public class App {

    static UserService userService;
    static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        get("/fun_1", (request, res) -> {
            request.session().attribute("user", "foobar");
            return "fun_1";
        });
        get("/fun_2", (request, res) -> {
            return request.session().attributes();
        });

        Datastore ds = getDatastore();
        App.userService = new UserService(ds);

        new Authorisation();
        new UserResource(App.userService);
        //new PersonResource(App.person_service);
        //new RealtyResource(App.realty_service);
        //new RequestResource(App.request_service);
    }

    private static Datastore getDatastore() throws Exception {
        MongoClient mongoClient = new MongoClient("localhost");

        Morphia morphia = new Morphia();
        morphia.map(User.class);

        Datastore ds = morphia.createDatastore(mongoClient, "rplus-dev");


        return ds;
    }
}
