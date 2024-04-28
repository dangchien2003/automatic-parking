package util;

import com.automaticparking.config.ConfigDB;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class hibernateUtil extends ConfigDB {
    public static Session openSession() {
        return getSessionFactory().openSession();
    }
    public static void shutdown() {
        getSessionFactory().close();
    }


}
