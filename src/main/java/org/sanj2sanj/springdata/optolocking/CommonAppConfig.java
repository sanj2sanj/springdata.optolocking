package org.sanj2sanj.springdata.optolocking;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.SQLException;
import java.util.Properties;

public abstract class CommonAppConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        te.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        return te;
    }

    @Bean
    public SimpleNamingContextBuilder getContext() throws NamingException,
            SQLException {
        final SimpleNamingContextBuilder context = SimpleNamingContextBuilder
                .emptyActivatedContextBuilder();
        context.bind(Application.JAVA_COMP_ENV_DB, dataSource());
        context.bind(Application.JAVA_COMP_ENV_CF, connectionFactory());
        context.bind(Application.JAVA_COMP_ENV_QUEUE, queue());
        context.bind(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, new UserTransaction() {

            @Override
            public void begin() throws NotSupportedException, SystemException {
                // TODO Auto-generated method stub

            }

            @Override
            public void commit() throws RollbackException,
                    HeuristicMixedException, HeuristicRollbackException,
                    SecurityException, IllegalStateException, SystemException {
                // TODO Auto-generated method stub

            }

            @Override
            public void rollback() throws IllegalStateException,
                    SecurityException, SystemException {
                // TODO Auto-generated method stub

            }

            @Override
            public void setRollbackOnly() throws IllegalStateException,
                    SystemException {
                // TODO Auto-generated method stub

            }

            @Override
            public int getStatus() throws SystemException {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void setTransactionTimeout(int seconds)
                    throws SystemException {
                // TODO Auto-generated method stub

            }
        });
        context.activate();
        return context;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

//	@Bean
//	public PlatformTransactionManager platformTransactionManager(){ 
//		return new JtaTransactionManager();
//	}

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.H2);
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
        lemfb.setDataSource(dataSource());
        lemfb.setJpaVendorAdapter(jpaVendorAdapter());

        Properties props = new Properties();
        props.put("eclipselink.weaving", "false");
        props.put("eclipselink.logging.level.sql", "OFF");
        props.put("eclipselink.logging.parameters", "true");
        props.put("eclipselink.ddl-generation.output-mode", "both");
        props.put("eclipselink.ddl-generation", "create-tables");

        lemfb.setJpaProperties(props);
        lemfb.setPackagesToScan(this.getClass().getPackage().getName());
        return lemfb;
    }

    @Bean
    public abstract ConnectionFactory connectionFactory();

    @Bean
    public abstract Queue queue();

    @Bean
    public abstract JmsTemplate jmsTemplate();

}
