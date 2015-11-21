package org.sanj2sanj.springdata.optolocking;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.usage.SystemUsage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.sql.SQLException;

@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableJpaAuditing
public class JUnitConfig extends CommonAppConfig {

    private static final String INBOUND_QUEUE = "queueName";
    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";

    @Bean
    public AuditorAware<AuditableUser> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws SQLException {
        return new JdbcTemplate(dataSource());
    }

    @Override
    @Bean
    public Queue queue() {
        return new ActiveMQQueue(INBOUND_QUEUE);
    }

    @Override
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setDefaultDestination(queue());
        jmsTemplate.setReceiveTimeout(500);
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory aq = new ActiveMQConnectionFactory(BROKER_URL);
        org.apache.activemq.RedeliveryPolicy rp = new org.apache.activemq.RedeliveryPolicy();
        rp.setInitialRedeliveryDelay(1000);
        rp.setMaximumRedeliveries(100);
        rp.setUseExponentialBackOff(true);
        aq.setRedeliveryPolicy(rp);
        return aq;
    }

    // @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();

        broker.addConnector(BROKER_URL);
        SystemUsage systemUsage = broker.getSystemUsage();
        systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
        systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);
        systemUsage.getMemoryUsage().setLimit(1024 * 1024 * 8);

        Object b = broker.getPersistenceAdapter();
        if (b instanceof KahaDBPersistenceAdapter) {
            ((KahaDBPersistenceAdapter) b)
                    .setJournalMaxFileLength(1024 * 1024 * 8);
        }
        broker.setPersistent(false);
        broker.start();
        return broker;
    }

    @Bean
    public RollingBackListener rollingBackListener() {
        return new RollingBackListener();
    }

    @Bean
    public DefaultMessageListenerContainer defaultMessageListenerContainer() {
        DefaultMessageListenerContainer d = new DefaultMessageListenerContainer();
        d.setConnectionFactory(connectionFactory());
        d.setDestination(queue());
        d.setSessionTransacted(true);
        d.setMessageListener(rollingBackListener());
        return d;
    }

}
