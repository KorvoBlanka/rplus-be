package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hibernate.entity.Request;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

        List<Request> requestList;

        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Request> cq = cb.createQuery(Request.class);
        Root<Request> requestRoot = cq.from(Request.class);
        cq.select(requestRoot);

        requestList = em.createQuery(cq).getResultList();

        return requestList;
    }

    public Request get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Request result = em.find(Request.class, id);

        em.close();


        return result;
    }

    public Request save (Request request) throws Exception {

        this.logger.info("save");

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Request result;

        em.getTransaction().begin();
        result = em.merge(request);
        em.getTransaction().commit();


        em.close();

        return result;
    }

    public Request delete (String id) {
        throw new NotImplementedException();
    }
}
