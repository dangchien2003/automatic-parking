package com.automaticparking.model.code.customer;

import com.automaticparking.model.customer.Customer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import util.Genarate;
import util.hibernateUtil;

import java.sql.SQLException;
import java.util.List;

@Repository
public class CodeRepository {
    public List<Code> getAllCodeUse(String uid) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * FROM qr WHERE uid = :uid AND ((cancleAt = 0 AND expireAt > :now) OR (checkinAt != 0))";
            NativeQuery<Code> query = session.createNativeQuery(sql, Code.class);
            query.setParameter("uid", uid);
            long now = Genarate.getTimeStamp();
            query.setParameter("now", now);
            List<Code> codeUsed = query.list();
            tr.commit();
            return codeUsed;
        } catch (Exception e) {
            tr.rollback();
            System.out.println(e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    public List<Code> allBoughtCode(String uid, Integer limit) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT *, 'Code' as DTYPE FROM qr WHERE uid = :uid ORDER BY buyAt DESC LIMIT 0, :limit";
            NativeQuery<Code> query = session.createNativeQuery(sql, Code.class);
            query.setParameter("uid", uid);
            query.setParameter("limit", limit);
            List<Code> boughtCode = query.list();
            tr.commit();
            return boughtCode;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<Code> getInfoByPlate(String plate) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * FROM qr WHERE plate = :plate";
            NativeQuery<Code> query = session.createNativeQuery(sql, Code.class);
            query.setParameter("plate", plate);
            List<Code> boughtCode = query.list();
            tr.commit();
            return boughtCode;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    Boolean saveCode(Code code) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(code);
            tr.commit();
            return true;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public Integer getToTalMoneyUsed(List<Code> codes) {
        Integer totalMyCash = 0;
        if (codes != null) {
            for (Code code : codes) {
                totalMyCash += (code.getPrice() + code.getPriceExtend());
            }
        }
        return totalMyCash;
    }

    public Ticket getInfo(String uid, String qrid) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = session.beginTransaction();

        try {
            String sql = "SELECT qr.*, bot.address FROM qr left join bot on bot.id = qr.botid WHERE  uid = :uid and qrid = :qrid";
            NativeQuery<Ticket> query = session.createNativeQuery(sql, Ticket.class);
            query.setParameter("uid", uid);
            query.setParameter("qrid", qrid);
            Ticket ticket = query.uniqueResult();
            return ticket;
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            tr.commit();
            session.close();
        }
    }

    public Code getInfo(String qrid) {
        Session session = hibernateUtil.openSession();
        Transaction tr = session.beginTransaction();

        try {
            String sql = "SELECT * from qr where qrid = :qrid";
            NativeQuery<Code> query = session.createNativeQuery(sql, Code.class);
            query.setParameter("qrid", qrid);
            Code code = query.uniqueResult();
            return code;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            tr.commit();
            session.close();
        }
    }

    public Boolean updateCode(Code code) throws SQLException {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.update(code);
            tr.commit();
            return true;
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }


    }
}
