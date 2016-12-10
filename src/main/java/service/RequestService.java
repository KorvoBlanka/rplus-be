package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hibernate.entity.Request;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManagerFactory;
import java.util.LinkedList;
import java.util.List;


public class RequestService {

    Logger logger = LoggerFactory.getLogger(RequestService.class);

    EntityManagerFactory emf;
    private final Client elasticClient;

    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "requests";

    Gson gson = new GsonBuilder().create();


    public RequestService (EntityManagerFactory emf, Client elasticClient) {

        this.emf = emf;
        this.elasticClient = elasticClient;
    }


    public List<Request> list (int page, int perPage, String personId, String searchQuery) {

        logger.info("list");

        List<Request> resList = new LinkedList<>();


        return resList;
    }

    public Request get (long id) {

        this.logger.info("get");

        Request res = null;

        return res;
    }

    public Request save (Request request) throws Exception {

        this.logger.info("save");

        Request res = null;

        return res;
    }

    public Request delete (String id) {
        throw new NotImplementedException();
    }
}
