package com.automaticparking.repositorys;


import com.automaticparking.database.entity.Cash;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class SCashRepository {
    public List<Cash> getAllCashNotApprove() throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM historycash where acceptAt is NULL and cancleAt is NULL";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
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

    public Integer approveListCash(Long[] listId, Long approveAt, String personApprove) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "UPDATE Cash SET acceptAt = :acceptAt, acceptBy = :acceptBy WHERE stt IN (:listId) and acceptAt IS NULL and cancleAt IS NULL";
            Query query = session.createQuery(sql);
            query.setParameter("acceptAt", approveAt);
            query.setParameter("acceptBy", personApprove);
            query.setParameter("listId", Arrays.asList(listId));
            Integer rowsAffected = query.executeUpdate();

            // kiem tra so luong id đã update
            if (rowsAffected != listId.length) {
                tr.rollback();
            } else {
                tr.commit();
            }
            return rowsAffected;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }
}
