package ru.perm.kefir.dating.security;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.perm.kefir.dating.Account;

/**
 * @author Vitaliy Samolovskih aka Kefir
 */
public class DatingSaltSource implements SaltSource {
    private PlatformTransactionManager transactionManager;
    private SessionFactory sessionFactory;

    @Override
    public Object getSalt(final UserDetails userDetails) {
        return new TransactionTemplate(transactionManager).execute(
                new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus transactionStatus) {
                        org.hibernate.classic.Session session = sessionFactory.getCurrentSession();
                        Criteria criteria = session.createCriteria(Account.class)
                                .add(Restrictions.eq("mail", userDetails.getUsername()));
                        Account account = (Account) criteria.uniqueResult();
                        if (account != null) {
                            return account.getSalt();
                        } else {
                            return null;
                        }
                    }
                }
        );
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
