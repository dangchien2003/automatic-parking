package com.automaticparking.model.cash;

import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.http.HttpStatus;
import util.hibernateUtil;

import java.util.List;

public class CashService {
    public List<Cash> getAllCashNotApproved() {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM historycash where acceptAt is NULL";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
            List<Cash> cashs = query.list();
            tr.commit();
            session.close();
            return cashs;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
