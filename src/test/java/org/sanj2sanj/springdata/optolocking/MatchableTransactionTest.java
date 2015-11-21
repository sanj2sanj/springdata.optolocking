package org.sanj2sanj.springdata.optolocking;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sanj2sanj.springdata.optolocking.domain.Matchable;
import org.sanj2sanj.springdata.optolocking.domain.MatchableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class})
public class MatchableTransactionTest {

    @Autowired
    MatchableRepository repository;

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void test_save_a_good_message() throws InterruptedException {
        Matchable carter = new Matchable("Carter", "Beauford");
        Gson gson = new Gson();
        String json = gson.toJson(carter);

        jmsTemplate.convertAndSend(json);
        Thread.sleep(5000);
        assertEquals(1, repository.findByText("Beauford").size());
    }


}
