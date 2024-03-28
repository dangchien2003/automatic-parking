package config;

import model.CatEntity;
import io.github.cdimascio.dotenv.Dotenv;
import model.test.abcEntity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;
import java.util.Properties;

public class configDB {
    private static MetadataSources metadataSources;

    public configDB() {
        metadataSources = null;
        setup();
    }

    private final void setup() {
        Properties properties = createHibernateProperties();
        StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();

        try {
            metadataSources = new MetadataSources(standardServiceRegistry);
            // all entity
             metadataSources.addAnnotatedClass(abcEntity.class);
            System.out.println("hibernate OK");
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
            System.out.println("Lỗi setup hibernate");
        }
    }

    public static void AddEntity(Class<?> add) {
        metadataSources.addAnnotatedClass(add);
    }


    public static SessionFactory getSessionFactory() {
        return metadataSources.buildMetadata().buildSessionFactory();
    }

    private Properties createHibernateProperties() {
        Dotenv dotenv = Dotenv.load();
        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String database = dotenv.get("DATABASE");
        String dbUser = dotenv.get("DB_USERNAME");
        String dbPass = dotenv.get("DB_PASSWORD");
        String showSql = dotenv.get("SHOW_SQL");

        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:mysql://"+dbHost+":"+dbPort+"/"+database);
        properties.setProperty("hibernate.connection.username", dbUser);
        properties.setProperty("hibernate.connection.password", dbPass);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.setProperty("hibernate.show_sql", showSql);

        return properties;
    }
}