package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

import java.util.List;

@Service
public class CashCustomerService {
    public Boolean saveCashHistory(Cash cash) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(cash);
            tr.commit();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        finally {
            session.close();
        }
        return  true;
    }

    public List<Cash> getALlMyHistory(String uid) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM historycash where uid = :uid";
            NativeQuery<Cash> query = session.createNativeQuery(sql, Cash.class);
            query.setParameter("uid", uid);
            List<Cash> cashs = query.list();
            tr.commit();
            session.close();
            return cashs;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
