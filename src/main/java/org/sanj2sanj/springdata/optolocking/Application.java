package org.sanj2sanj.springdata.optolocking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Arrays;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static final String JAVA_COMP_ENV_DB = "java:comp/env/db";
    public static final String JAVA_COMP_ENV_CF = "java:comp/env/cf";
    public static final String JAVA_COMP_ENV_QUEUE = "java:comp/env/queue";

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

    }

    @Bean
    public DefaultMessageListenerContainer defaultMessageListenerContainer()
            throws NamingException {
        DefaultMessageListenerContainer mlc = new DefaultMessageListenerContainer();
        mlc.setMessageListener(exampleListener());
        mlc.setConnectionFactory(connectionFactory());
        mlc.setDestination(queue());
        return mlc;
    }

    @Bean
    public RollingBackListener exampleListener() {
        return new RollingBackListener();
    }

    private Destination queue() throws NamingException {
        return (Destination) jndiTemplate().lookup(JAVA_COMP_ENV_QUEUE);
    }

    private ConnectionFactory connectionFactory() throws NamingException {
        return (ConnectionFactory) jndiTemplate().lookup(JAVA_COMP_ENV_CF);
    }

    @Bean
    JndiTemplate jndiTemplate() {
        return new JndiTemplate();
    }

    @Bean
    public DataSource getDataSource() throws NamingException {
        return (DataSource) jndiTemplate().lookup(JAVA_COMP_ENV_DB);
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() throws NamingException {
        return new JdbcTemplate(getDataSource());
    }
}
