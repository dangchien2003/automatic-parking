package com.automaticparking.model.bot;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

import java.sql.SQLException;

@Repository
public class BotRepository {
    public Bot getInfo(String id) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * From bot where id = :id";
            NativeQuery<Bot> query = session.createNativeQuery(sql, Bot.class);
            query.setParameter("id", id);
            Bot qr = query.uniqueResult();
            tr.commit();
            return qr;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }
}
