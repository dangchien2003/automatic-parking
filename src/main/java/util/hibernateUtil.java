package util;

import config.configDB;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class hibernateUtil extends configDB {
    public static void shutdown() {
        getSessionFactory().close();
    }

}
