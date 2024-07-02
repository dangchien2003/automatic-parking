package com.automaticparking.model.cash.staff;


import com.automaticparking.model.cash.Cash;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

import java.util.Arrays;
import java.util.List;

@Repository
public class CashStaffRepository {
    public List<Cash> getAllCashNotApprove() {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM historycash where acceptAt is NULL";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
            List<Cash> cashs = query.list();
            tr.commit();
            session.close();
            return cashs;
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Integer approveListCash(Long[] listId, Long approveAt, String personApprove) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
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

            session.close();
            return rowsAffected;
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
