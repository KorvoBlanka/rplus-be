/**
 * Created by owl on 3/23/16.
 */

import Configuration.AppConfig;
import com.mongodb.*;
import morphia.entity.User;
import morphia.entity.Offer;
import org.eclipse.jetty.server.Request;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.Maintenance;
import resource.OfferResource;
import resource.PhotoResource;
import resource.UserResource;
import service.ElasticService;
import service.OfferService;
import service.PhotoService;
import service.UserService;
import spark.Filter;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static spark.Spark.*;


public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);

    static ElasticService elasticService;

    static UserService userService;
    static OfferService offerService;
    static PhotoService photoService;

    public static void main(String[] args) throws Exception {

        AppConfig.LoadConfig();
        Spark.externalStaticFileLocation(AppConfig.STATIC_FILE_LOCATION);


        Client ec = getElasticClient();
        App.elasticService = new ElasticService(ec);

        Datastore ds = getDatastore();
        App.userService = new UserService(ec);
        App.offerService = new OfferService(ec);
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

    private static Client getElasticClient() {
        Settings settings = Settings.settingsBuilder().put("path.home", "./elastic/").build();

        Node node = nodeBuilder().settings(settings).node();
        Client client = node.client();

        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(5000);

        return client;
    }

    private static Datastore getDatastore() throws Exception {
        MongoClient mongoClient = new MongoClient("localhost");

        Morphia morphia = new Morphia();

        MapperOptions options = new MapperOptions();
        options.setStoreEmpties(true);
        options.setStoreNulls(true);
        morphia.getMapper().setOptions(options);

        Datastore ds = morphia.createDatastore(mongoClient, "rplus-dev");


        return ds;
    }

}
