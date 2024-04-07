package util;

import config.configDB;
public class hibernateUtil extends configDB {
    public static void shutdown() {
        getSessionFactory().close();
    }

}
