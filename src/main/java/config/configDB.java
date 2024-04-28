package config;

import com.automaticparking.model.cash.staff.Cash;
import com.automaticparking.model.customer.Customer;
import com.automaticparking.model.staff.Staff;
import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class configDB {
    private static MetadataSources metadataSources;
    Dotenv dotenv = Dotenv.load();
    public configDB() {
        metadataSources = null;
        setup();
    }

    private final void setup() {
        Properties properties = null;
        switch (dotenv.get("ACTIVE")) {
            case "dev":
                properties = createHibernatePropertiesDev();
                break;
            case "prod":
                properties = createHibernateProperties();
                break;
            default:
                System.out.println("cannot found active");
                return;
        }
        StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();

        try {
            metadataSources = new MetadataSources(standardServiceRegistry);
            // all entity
            metadataSources.addAnnotatedClass(Staff.class);
            metadataSources.addAnnotatedClass(Cash.class);
            metadataSources.addAnnotatedClass(Customer.class);



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
        properties.setProperty("hibernate.connection.pool_size", "10");

        return properties;
    }

    private Properties createHibernatePropertiesDev() {
        String dbHost = dotenv.get("DB_HOST_DEV");
        String dbPort = dotenv.get("DB_PORT_DEV");
        String database = dotenv.get("DATABASE_DEV");
        String dbUser = dotenv.get("DB_USERNAME_DEV");
        String dbPass = dotenv.get("DB_PASSWORD_DEV");
        String showSql = dotenv.get("SHOW_SQL_DEV");

        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:mysql://"+dbHost+":"+dbPort+"/"+database);
        properties.setProperty("hibernate.connection.username", dbUser);
        properties.setProperty("hibernate.connection.password", dbPass);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.connection.pool_size", "10");

        return properties;
    }
}