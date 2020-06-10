package com.mawodu.levram;

import com.mawodu.levram.clients.MarvelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

// to store credentials preference to use: https://spring.io/guides/gs/vault-config/

@Configuration
@PropertySource({"classpath:application.properties"})
public class CoreConfig {

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TenantBeanFactoryPostProcessor();
    }

    @Autowired
    Environment env;

    @Autowired
    TaskExecutor taskExecutor;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.valueOf(env.getProperty("cores.pool.size")));
        executor.setMaxPoolSize(Integer.valueOf(env.getProperty("threads.pool.size")));
        executor.setThreadNamePrefix("fetch-store");
        executor.initialize();
        return executor;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Autowired
    HeroStore heroStore(JdbcTemplate jdbcTemplate) {
        return new HeroStore(jdbcTemplate);
    }

    @Bean
    @Scope(scopeName = "thread")
    MarvelClient marvelClient() {
        return new MarvelClient();
    }

    @Bean
    @Qualifier("marvel")
    @Scope(scopeName = "thread")
    public ClientHandler marvelClientHandler() {
        return new ClientHandler(marvelClient());
    }

//    @Bean
//    @Autowired
//    public MarvelDataTrigger marvelDataTrigger(TaskExecutor taskExecutor, HeroStore store) {
//        return new MarvelDataTrigger(taskExecutor, store, marvelClientHandler());
//    }


}
