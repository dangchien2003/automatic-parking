package com.automaticparking.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Log4j
public class ConfigJPA {
    private Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"com.automaticparking"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        em.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "update");
        em.getJpaPropertyMap().put("hibernate.show_sql", "true");
//        em.getJpaPropertyMap().put("hibernate.format_sql", "true");
        em.getJpaPropertyMap().put("hibernate.use_sql_comments", "true");

        return em;
    }

    @Bean
    public DataSource dataSource() {
        String active = dotenv.get("ACTIVE");
        String host = "";
        String port = "";
        String database = "";
        String user = "";
        String password = "";
        if (active.equals("dev")) {
            host = dotenv.get("DB_HOST_DEV");
            port = dotenv.get("DB_PORT_DEV");
            database = dotenv.get("DATABASE_DEV");
            user = dotenv.get("DB_USERNAME_DEV");
            password = dotenv.get("DB_PASSWORD_DEV");
        } else if (active.equals("prod")) {
            host = dotenv.get("DB_HOST");
            port = dotenv.get("DB_PORT");
            database = dotenv.get("DATABASE");
            user = dotenv.get("DB_USERNAME");
            password = dotenv.get("DB_PASSWORD");
        } else {
            log.error("Invalid active: " + active);
            System.out.println("active: " + active);
        }
        return DataSourceBuilder.create()
                .url(String.format("jdbc:mysql://%s:%s/%s", host, port, database))
                .username(user)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }


}
