package service;

import hibernate.entity.Account;
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

/**
 * Created by Aleksandr on 20.01.17.
 */
public class AccountService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    EntityManagerFactory emf;

    public AccountService (EntityManagerFactory emf) {

        this.emf = emf;
    }

    public List<Account> list (String searchQuery) {

        this.logger.info("list");

        List<Account> accountList;

        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> userRoot = cq.from(Account.class);
        cq.select(userRoot);

        accountList = em.createQuery(cq).getResultList();

        em.close();

        return accountList;
    }

    public Account get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Account result = em.find(Account.class, id);

        em.close();

        return result;
    }


    public Account save (Account account) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Account result;

        em.getTransaction().begin();
        result = em.merge(account);
        em.getTransaction().commit();

        em.close();

        return result;
    }

    public Account delete (long id) {
        throw new NotImplementedException();
    }

}
