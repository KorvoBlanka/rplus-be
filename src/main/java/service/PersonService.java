package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import hibernate.entity.Organisation;
import hibernate.entity.Request;
import hibernate.entity.User;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import hibernate.entity.Person;
import utils.CommonUtils;
import utils.FilterObject;


public class PersonService {

    Logger logger = LoggerFactory.getLogger(PersonService.class);
    EntityManagerFactory emf;
    private final Client elasticClient;

    public PersonService (EntityManagerFactory emf, Client elasticClient) {

        this.emf = emf;
        this.elasticClient = elasticClient;
    }

    public List<String> check (Person person) {
        List<String> errors = new LinkedList<>();

        //if ((person.getPhones() == null || person.getPhones().length == 0) && (person.getEmails() == null || person.getEmails().length == 0)) errors.add("no phones and no emails given");

        return errors;
    }

    public List<Person> list (Long accountId, int page, int perPage, Integer userId, Integer organisationId, String searchQuery) {

        logger.info("list");

        /*
        List<Person> personList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);



        List<Predicate> predicates = new ArrayList<Predicate>();

        if (accountId != null) {
            predicates.add(cb.equal(personRoot.get("accountId"), accountId));
        }

        if (userId != null) {
            predicates.add(cb.equal(personRoot.get("userId"), userId));
        }

        if (organisationId != null) {
            predicates.add(cb.equal(personRoot.get("organisationId"), organisationId));
        }

        cq.select(personRoot).where(predicates.toArray(new Predicate[]{}));
        personList = em.createQuery(cq).getResultList();
        */

        List<Person> offerList = new ArrayList<>();

        EntityManager em = emf.createEntityManager();

        SearchRequestBuilder rb = elasticClient.prepareSearch("rplus")
                .setTypes("person")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));
        if (userId != null) {
            q.must(QueryBuilders.termQuery("userId", userId));
        }
        if (organisationId != null) {
            q.must(QueryBuilders.termQuery("organisationId", organisationId));
        }


        if (searchQuery != null && searchQuery.length() > 2) {

            q.should(QueryBuilders.matchQuery("name", searchQuery));
            q.should(QueryBuilders.matchQuery("phones", searchQuery));
            q.should(QueryBuilders.matchQuery("emails", searchQuery));
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        List<Person> personList = new ArrayList<>();

        for (SearchHit sh: response.getHits()) {
            Person person = em.find(hibernate.entity.Person.class, Long.parseLong(sh.getId()));
            if (person != null) {
                personList.add(person);
            }
        }

        return personList;
    }

    public Person get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Person result = em.find(Person.class, id);


        em.close();

        return result;
    }

    public Person save (Person person) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Person result;

        em.getTransaction().begin();
        result = em.merge(person);
        em.getTransaction().commit();

        em.close();

        indexPerson(result);

        return result;
    }

    public void indexPerson(Person person) {

        Map<String, Object> json = new HashMap<String, Object>();

        json.put("name", person.getName());

        // filters
        json.put("accountId", person.getAccountId());
        json.put("agentId", person.getUserId());
        json.put("organisationId", person.getOrganisation());

        ArrayList<String> phoneArray = new ArrayList<>();
        phoneArray.add(CommonUtils.strNotNull(person.getHomePhone_n()));
        phoneArray.add(CommonUtils.strNotNull(person.getCellPhone_n()));
        phoneArray.add(CommonUtils.strNotNull(person.getMainPhone_n()));
        phoneArray.add(CommonUtils.strNotNull(person.getOtherPhone_n()));
        phoneArray.add(CommonUtils.strNotNull(person.getOfficePhone_n()));

        String phones = String.join(" ", phoneArray);
        json.put("phones", phones);

        ArrayList<String> mailArray = new ArrayList<>();
        phoneArray.add(CommonUtils.strNotNull(person.getWorkEmail_n()));
        phoneArray.add(CommonUtils.strNotNull(person.getMainEmail_n()));

        String mails = String.join(" ", phoneArray);
        json.put("emails", mails);


        IndexResponse response = this.elasticClient.prepareIndex("rplus", "person", Long.toString(person.getId())).setSource(json).get();
    }

    public Person delete (long id) {
        return null;
    }
}
