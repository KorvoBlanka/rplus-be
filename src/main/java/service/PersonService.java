package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import hibernate.entity.Organisation;
import hibernate.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;

import hibernate.entity.Person;



public class PersonService {

    Logger logger = LoggerFactory.getLogger(PersonService.class);
    EntityManagerFactory emf;


    public PersonService (EntityManagerFactory emf) {

        this.emf = emf;
    }

    public List<String> check (Person person) {
        List<String> errors = new LinkedList<>();

        if ((person.getPhones() == null || person.getPhones().length == 0) && (person.getEmails() == null || person.getEmails().length == 0)) errors.add("no phones and no emails given");

        return errors;
    }

    public List<Person> list (int page, int perPage, Integer userId, Integer organisationId, String searchQuery) {

        logger.info("list");

        List<Person> personList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);
        cq.select(personRoot);

        if (userId != null) {
            cq.where(cb.equal(personRoot.get("userId"), userId));
        }

        if (organisationId != null) {
            cq.where(cb.equal(personRoot.get("organisationId"), organisationId));
        }

        personList = em.createQuery(cq).getResultList();

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

        return result;
    }

    public Person delete (long id) {
        throw new NotImplementedException();
    }
}
