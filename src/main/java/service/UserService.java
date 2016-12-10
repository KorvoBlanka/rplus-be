package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public List<User> list (User.Role role, Integer superiorId, String searchQuery) {

        this.logger.info("list");

        List<User> userList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.select(userRoot);

        if (role != null) {
            cq.where(cb.equal(userRoot.get("role"), role));
        }

        if (superiorId != null) {
            cq.where(cb.equal(userRoot.get("superiorId"), superiorId));
        }

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

    public User getByLogin (String login) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        User result = null;

        result = em.createQuery("FROM User WHERE login = :login", User.class).setParameter("login", login).getSingleResult();

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
        throw new NotImplementedException();
    }
}
