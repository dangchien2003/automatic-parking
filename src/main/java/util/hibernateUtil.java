package util;

import config.configDB;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class hibernateUtil extends configDB{
    public static Session openSession() {
        return getSessionFactory().openSession();
    }
    public static void shutdown() {
        getSessionFactory().close();
    }


}
