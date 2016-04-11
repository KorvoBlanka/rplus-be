/**
 * Created by owl on 3/23/16.
 */

import Configuration.AppConfig;
import com.mongodb.*;
import morphia.entity.User;
import morphia.entity.Offer;
import org.eclipse.jetty.server.Request;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.Maintenance;
import resource.OfferResource;
import resource.PhotoResource;
import resource.UserResource;
import service.OfferService;
import service.PhotoService;
import service.UserService;
import spark.Filter;
import spark.Spark;

import static spark.Spark.*;


public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);

    static UserService userService;
    static OfferService offerService;
    static PhotoService photoService;

    public static void main(String[] args) throws Exception {

        AppConfig.LoadConfig();
        Spark.externalStaticFileLocation(AppConfig.STATIC_FILE_LOCATION);

        Datastore ds = getDatastore();
        App.userService = new UserService(ds);
        App.offerService = new OfferService(ds);
        App.photoService = new PhotoService(ds);

        new Authorisation();
        new Maintenance(App.offerService, App.photoService);
        new UserResource(App.userService);
        new OfferResource(App.offerService);
        new PhotoResource(App.photoService);
        //new PersonResource(App.person_service);
        //new RealtyResource(App.realty_service);
        //new RequestResource(App.request_service);
    }

    private static Datastore getDatastore() throws Exception {
        MongoClient mongoClient = new MongoClient("localhost");

        Morphia morphia = new Morphia();

        MapperOptions options = new MapperOptions();
        options.setStoreEmpties(true);
        options.setStoreNulls(true);
        morphia.getMapper().setOptions(options);

        morphia.map(User.class);
        morphia.map(Offer.class);

        Datastore ds = morphia.createDatastore(mongoClient, "rplus-dev");


        return ds;
    }

}
