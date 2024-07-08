package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.code.customer.Code;
import com.automaticparking.model.code.customer.CodeRepository;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

import java.util.List;

@Service
public class CashCustomerRepository {
    private final CodeRepository codeRepository = new CodeRepository();

    public Boolean saveCashHistory(Cash cash) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(cash);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            System.out.println(e.getMessage());
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    public List<Cash> getALlMyHistory(String uid) {
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
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public List<Cash> getALlMyHistoryOk(String uid) {
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
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public Integer getTotalCash(List<Cash> historyCash) {
        Integer totalMyCash = 0;
        if (historyCash != null) {
            for (Cash cash : historyCash) {
                totalMyCash += cash.getMoney();
            }
        }
        return totalMyCash;
    }

    public Integer getRemaining(String uid) {
        // get history plus cash
        List<Cash> historyCash = getALlMyHistoryOk(uid);

        Integer totalMyCash = getTotalCash(historyCash);
        // get history code used
        List<Code> myCode = codeRepository.getAllCodeUse(uid);

        Integer moneyUsed = codeRepository.getToTalMoneyUsed(myCode);
        // get remaining(số dư)
        Integer remaining = totalMyCash - moneyUsed;

        return remaining;
    }
}
