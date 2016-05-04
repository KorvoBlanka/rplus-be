/**
 * Created by owl on 3/23/16.
 */

import Configuration.AppConfig;
import com.mongodb.*;
import entity.Request;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.*;
import service.*;
import spark.Spark;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;


public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);

    static ElasticService elasticService;

    static UserService userService;
    static OrganisationService orgService;
    static PersonService personService;
    static OfferService offerService;
    static RequestService requestService;

    static PhotoService photoService;

    public static void main(String[] args) throws Exception {

        AppConfig.LoadConfig();
        Spark.externalStaticFileLocation(AppConfig.STATIC_FILE_LOCATION);


        Client ec = getElasticClient();
        App.elasticService = new ElasticService(ec);

        Datastore ds = getDatastore();

        // services
        App.userService = new UserService(ec);
        App.orgService = new OrganisationService(ec);
        App.personService = new PersonService(ec);
        App.offerService = new OfferService(ec);
        App.requestService = new RequestService(ec);

        App.photoService = new PhotoService(ds);

        // resources
        new Authorisation();
        new Maintenance(App.offerService, App.photoService);
        new UserResource(App.userService);
        new OrganisationResource(App.orgService);
        new PersonResource(App.personService);
        new OfferResource(App.offerService);
        new RequestResource(App.requestService);

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
