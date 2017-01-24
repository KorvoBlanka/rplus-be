package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import hibernate.entity.User;



public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    EntityManagerFactory emf;

    public UserService (EntityManagerFactory emf) {

        this.emf = emf;
    }

    public List<String> check (User user) {
        // check login, pass, role
        List<String> errors = new LinkedList<>();

        if (user.getLogin() == null || user.getLogin().length() < 4) errors.add("login is null or too short");
        if (user.getPassword() == null || user.getPassword().length() < 4) errors.add("password is null or too short");
        if (user.getRole() == null) {
            errors.add("unknown role ");
        }

        return errors;
    }

    public List<User> list (Integer accountId, User.Role role, Integer superiorId, String searchQuery) {

        this.logger.info("list");

        List<User> userList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);


        List<Predicate> predicates = new ArrayList<Predicate>();

        if (accountId != null) {
            predicates.add(cb.equal(userRoot.get("accountId"), accountId));
            //cq = cq.where();
        }

        if (role != null) {
            predicates.add(cb.equal(userRoot.get("role"), role));
        }

        if (superiorId != null) {
            predicates.add(cb.equal(userRoot.get("superiorId"), superiorId));
        }

        cq.select(userRoot)
                .where(predicates.toArray(new Predicate[]{}));

        userList = em.createQuery(cq).getResultList();

        em.close();

        return userList;
    }

    public User get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        User result = em.find(User.class, id);

        em.close();

        return result;
    }

    public User getByLogin (Long accountId, String login) {

        this.logger.info("get by login");

        EntityManager em = emf.createEntityManager();

        User result = null;

        List<User> l = em.createQuery("FROM User WHERE accountId = :accountId AND login = :login", User.class).setParameter("accountId", accountId).setParameter("login", login).getResultList();

        if (l.size() > 0) {
            result = l.get(0);
        }

        em.close();

        return result;
    }

    public User save (User user) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        User result;

        em.getTransaction().begin();
        result = em.merge(user);
        em.getTransaction().commit();

        em.close();

        return result;
    }

    public User delete (long id) {
    }
}
