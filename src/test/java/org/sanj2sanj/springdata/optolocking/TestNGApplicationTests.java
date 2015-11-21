package org.sanj2sanj.springdata.optolocking;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(classes = {JUnitConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class TestNGApplicationTests extends AbstractTestNGSpringContextTests {

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void contextLoads() {
        for (int i = 0; i < 100; i++) {
            jmsTemplate.convertAndSend("Hello");
        }
    }

    @Test
    public void contextLoads2() {
        for (int i = 0; i < 100; i++) {
            jmsTemplate.convertAndSend("Hello testng ");
        }
    }
}

