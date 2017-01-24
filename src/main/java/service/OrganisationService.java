package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

import hibernate.entity.Organisation;


public class OrganisationService {

    Logger logger = LoggerFactory.getLogger(OrganisationService.class);
    EntityManagerFactory emf;


    public OrganisationService (EntityManagerFactory emf) {

        this.emf = emf;
    }

    public List<String> check (Organisation org) {
        List<String> errors = new LinkedList<>();

        if (org.getName() == null || org.getName().length() < 2) errors.add("name is null or too short");

        return errors;
    }

    public List<Organisation> list (int page, int perPage, String searchQuery) {

        this.logger.info("list");

        List<Organisation> orgList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Organisation> cq = cb.createQuery(Organisation.class);
        Root<Organisation> organiasationRoot = cq.from(Organisation.class);
        cq.select(organiasationRoot);


        orgList = em.createQuery(cq).getResultList();


        em.close();

        return orgList;
    }

    public Organisation get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Organisation result = em.find(Organisation.class, id);

        em.close();

        return result;
    }

    public Organisation save (Organisation organisation) throws Exception {

        this.logger.info("create");


        EntityManager em = emf.createEntityManager();

        Organisation result;

        em.getTransaction().begin();
        result = em.merge(organisation);
        em.getTransaction().commit();


        em.close();

        return result;
    }

    public Organisation delete (long id) {
    }
}
