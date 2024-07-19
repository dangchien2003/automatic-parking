package com.automaticparking.repositorys;

import com.automaticparking.database.entity.Cash;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

import java.sql.SQLException;
import java.util.List;

@Service
public class CCashRepository {


    public Boolean saveCashHistory(Cash cash) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(cash);
            tr.commit();
            return true;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<Cash> getALlMyHistory(String uid) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * FROM historycash where uid = :uid ORDER BY cashAt DESC";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
            query.setParameter("uid", uid);
            List<Cash> cashs = query.list();
            tr.commit();
            return cashs;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<Cash> getALlMyHistoryOk(String uid) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * FROM historycash WHERE uid = :uid AND acceptAt IS NOT NULL AND recashAt IS NULL AND cancleAt is null";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
            query.setParameter("uid", uid);
            List<Cash> cashs = query.list();
            tr.commit();
            return cashs;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }


}
