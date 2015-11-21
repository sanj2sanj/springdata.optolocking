package org.sanj2sanj.springdata.optolocking;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sanj2sanj.springdata.optolocking.domain.Child;
import org.sanj2sanj.springdata.optolocking.domain.MatchRepository;
import org.sanj2sanj.springdata.optolocking.domain.MatchableRepository;
import org.sanj2sanj.springdata.optolocking.domain.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class})
public class ParentChildMatchingTest {

    @Autowired
    MatchableRepository matchables;

    @Autowired
    MatchRepository matches;

    @Autowired
    CreateMatchTask matcher1;

    @Autowired
    CreateMatchTask matcher2;

    @Autowired
    ApplicationContext ctx;

    @Autowired
    ThreadPoolTaskExecutor te;

    @Before
    public void before() {
        matches.deleteAll();
        matchables.deleteAll();
        Parent dad = new Parent();
        dad.setFirstName("Homer");
        dad.setLastName("Simpson");

        Child son = new Child();
        son.setFirstName("Bart");
        son.setLastName("Simpson");

        matchables.save(son);
        matchables.save(dad);
    }

    @Test
    public void test_lots_of_threads_only_one_should_succeed() throws InterruptedException, ExecutionException {
        int NUM_OF_THREADS = 60;

        CountDownLatch latch = new CountDownLatch(1);
        CreateMatchTask ctm = null;
        List<FutureTask<?>> tasks = Lists.newArrayList();
        while (NUM_OF_THREADS != 0) {
            NUM_OF_THREADS--;
            ctm = ctx.getBean(CreateMatchTask.class);

            ctm.setLatch(latch);

            FutureTask<Exception> ft = new FutureTask<Exception>(ctm);
            tasks.add(ft);
            te.execute(ft);
        }
        latch.countDown();
        Thread.sleep(1000);
        waitForTasksToComplete(tasks);

        Multiset<String> multiset = getTaskResults(tasks);
        Logger.getAnonymousLogger().info(multiset.toString());
        int count = multiset.count(Boolean.TRUE.toString());
        assertTrue(count > 0);
        assertEquals(matches.count(), count);
        Logger.getAnonymousLogger().info(matchables.findAll().toString());
    }

    private void waitForTasksToComplete(List<FutureTask<?>> tasks)
            throws InterruptedException {
        while (true) {
            int count = 0;
            for (FutureTask<?> t : tasks) {
                if (t.isDone()) {
                    count++;
                }
            }
            if (count == tasks.size()) {
                break;
            }
            Thread.sleep(3000);
        }
    }

    private Multiset<String> getTaskResults(List<FutureTask<?>> tasks)
            throws InterruptedException, ExecutionException {
        Multiset<String> multiset = HashMultiset.create();
        for (FutureTask<?> t : tasks) {
            Object exception = t.get();
            if (exception != null) {
                multiset.add(Boolean.FALSE.toString());
            } else {
                multiset.add(Boolean.TRUE.toString());
            }
        }
        return multiset;
    }

    @Test
    public void test_two_conflicting_matches_happening_at_the_same_time() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        matcher1.setLatch(latch);
        matcher2.setLatch(latch);
        FutureTask<?> task1 = new FutureTask<Exception>(matcher1);
        FutureTask<?> task2 = new FutureTask<Exception>(matcher2);
        te.execute(task1);
        te.execute(task2);
        latch.countDown();

        waitForTasksToComplete(Arrays.asList(task1, task2));

        Multiset<Boolean> results = HashMultiset.create();
        results.add(matcher1.success);
        results.add(matcher2.success);
        assertEquals(1, results.count(true));
        assertEquals(1, results.count(false));

    }


}