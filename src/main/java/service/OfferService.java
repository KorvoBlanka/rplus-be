package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hibernate.entity.Offer;
import hibernate.entity.Person;
import hibernate.entity.User;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.FilterObject;
import utils.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;


public class OfferService {

    Logger logger = LoggerFactory.getLogger(OfferService.class);

    EntityManagerFactory emf;
    private final Client elasticClient;

    private final String E_INDEX = "rplus-index-dev";
    private final String E_TYPE = "offers";

    Gson gson = new GsonBuilder().create();


    public OfferService (EntityManagerFactory emf, Client elasticClient) {

        this.emf = emf;
        this.elasticClient = elasticClient;
    }

    public List<Offer> list (int page, int perPage, Map<String, Integer> filter, String searchQuery) {

        List<Offer> offerList;

        Map<String, String> queryParts = Query.process(searchQuery);
        List<FilterObject> filters = Query.parse(searchQuery);
        logger.info(gson.toJson(filters));

        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Offer> cq = cb.createQuery(Offer.class);
        Root<Offer> offerRoot = cq.from(Offer.class);
        cq.select(offerRoot);


        offerList = em.createQuery(cq).getResultList();

        return offerList;
    }

    public Offer get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Offer offer = em.find(hibernate.entity.Offer.class, id);

        em.close();

        return offer;
    }

    public Offer save (Offer offer) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Offer result;

        em.getTransaction().begin();
        result = em.merge(offer);
        em.getTransaction().commit();


        return result;
    }

    public Offer delete (int id) {
        throw new NotImplementedException();
    }
}
