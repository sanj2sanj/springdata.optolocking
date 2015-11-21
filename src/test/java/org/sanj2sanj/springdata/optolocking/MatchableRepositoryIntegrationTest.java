package org.sanj2sanj.springdata.optolocking;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sanj2sanj.springdata.optolocking.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class})
@Transactional
@DirtiesContext
public class MatchableRepositoryIntegrationTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MatchableRepository repository;

    @Autowired
    MatchRepository matches;
    @Autowired
    AuditorAwareImpl a;
    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    public void test_versioning_of_entity() {
        Matchable dave = new Matchable("Dave", "Matthews");
        dave = repository.save(dave);
        em.lock(dave, LockModeType.OPTIMISTIC);
        System.out.println(dave);

        Matchable carter = new Matchable("Carter", "Beauford");
        carter = repository.save(carter);

        List<Matchable> result = repository.findByText("Matthews");
        assertThat(result.size(), is(1));
        System.out.println(result.get(0));

        assertThat(result, hasItem(dave));

        assertThat(result.get(0).getVersion(), is(1L));
        dave = repository.save(dave);

    }

    @Test
    public void test_versioning_of_entity_after_changing_name() {
        Matchable dave = new Matchable("Dave", "Matthews");
        dave = repository.save(dave);
        dave = repository.findByText("Matthews").get(0);
        assertThat(dave.getVersion(), is(1L));
        dave.setLastname("xyz");
        dave = repository.saveAndFlush(dave);
        assertThat(dave.getVersion(), is(2L));
    }

    @Test
    public void test_save_matchables() {
        a.user = userRepo.saveAndFlush(new AuditableUser());

        Matchable parent = new Parent("the", "parent");
        Matchable child = new Child("the", "child");
        parent = repository.saveAndFlush(parent);
        child = repository.saveAndFlush(child);

        assertNotNull(parent.getId());
        assertNotNull(child.getId());

        Match match = new Match();
        match.add(parent);
        match.add(child);

        match = matches.saveAndFlush(match);

        Match searchedMatch = matches.findAll().iterator().next();
        assertEquals(2, searchedMatch.size());
        Logger.getAnonymousLogger().info(searchedMatch.getCreatedBy() + "");
        Logger.getAnonymousLogger().info(ObjectUtils.toString(searchedMatch));
        Logger.getAnonymousLogger().info(ObjectUtils.toString(userRepo.findAll()));
        assertNotNull(searchedMatch.getCreatedBy());
    }

}