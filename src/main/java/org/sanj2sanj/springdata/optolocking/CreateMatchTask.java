package org.sanj2sanj.springdata.optolocking;

import org.sanj2sanj.springdata.optolocking.domain.Match;
import org.sanj2sanj.springdata.optolocking.domain.MatchRepository;
import org.sanj2sanj.springdata.optolocking.domain.Matchable;
import org.sanj2sanj.springdata.optolocking.domain.MatchableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Component
@Scope("prototype")
public class CreateMatchTask implements Callable<Exception>, Runnable {

    public volatile boolean success = false;
    String name = UUID.randomUUID().toString();
    @Autowired
    MatchableRepository matchables;

    @Autowired
    MatchRepository matches;

    @Autowired
    JpaTransactionManager ptm;
    CountDownLatch latch;
    @PersistenceContext
    private EntityManager em;

    @Override
    public void run() {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("tx" + name);
        TransactionStatus s = ptm.getTransaction(def);

        Match match = new Match();
        Iterable<Matchable> all = matchables.findAll();
        for (Matchable m : all) {
            em.lock(m, LockModeType.OPTIMISTIC);
            m.incrementMatchAttempts();
            match.add(m);
        }

        matches.save(match);
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        try {
            ptm.commit(s);
            success = true;
            System.out.println("SUCCESS " + name);
        } catch (JpaOptimisticLockingFailureException o) {
            Logger.getAnonymousLogger().info("ole");
            throw o;
        }
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public Exception call() throws Exception {
        try {
            run();
        } catch (Exception e) {
            return e;
        }

        return null;
    }

}
