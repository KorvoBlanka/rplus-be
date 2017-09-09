/**
 * Created by owl on 3/23/16.
 */

import configuration.AppConfig;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.*;
import service.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static spark.Spark.exception;


public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);

    static TransportClient ec;

    static AccountService accountService;
    static UserService userService;
    static OrganisationService orgService;
    static PersonService personService;
    static OfferService offerService;
    static RequestService requestService;
    static GeoService geoService;
    static UploadService uploadService;


    public static void main(String[] args) throws Exception {

        AppConfig.LoadConfig();
        //Spark.externalStaticFileLocation(AppConfig.STATIC_FILE_LOCATION);

        ec = getElasticClient();

        // services
        App.accountService = new AccountService(ec);
        App.userService = new UserService(ec);
        App.orgService = new OrganisationService(ec);
        App.personService = new PersonService(ec);
        App.offerService = new OfferService(ec, userService, personService, orgService);
        App.requestService = new RequestService(ec);
        App.geoService = new GeoService();
        App.uploadService = new UploadService();

        // resources
        new Authorisation();
        new Maintenance(App.offerService, App.userService, App.personService);
        new AccountResource(App.accountService);
        new UserResource(App.userService);
        new OrganisationResource(App.orgService);
        new PersonResource(App.personService);
        new OfferResource(App.offerService);
        new RequestResource(App.requestService);
        new GeoResource(App.geoService);
        new UploadResource(App.uploadService);

        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("Ex:" + exception.getMessage());
            exception.printStackTrace();
        });
    }

    private static TransportClient getElasticClient() throws UnknownHostException {

        Settings settings = Settings.builder().put("cluster.name", "rplus-dev").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        return client;
    }
}
