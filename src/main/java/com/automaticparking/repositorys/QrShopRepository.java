package com.automaticparking.repositorys;

import com.automaticparking.database.entity.QrShop;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

import java.sql.SQLException;
import java.util.List;

@Service
public class QrShopRepository {
    public List<QrShop> getAllCodeOk() throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * FROM shopqr WHERE hide = 0";
            NativeQuery<QrShop> query = session.createNativeQuery(sql, QrShop.class);
            List<QrShop> qr = query.list();
            tr.commit();
            session.close();
            return qr;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public QrShop getOneQrById(String category) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            String sql = "SELECT * From shopqr where qrCategory = :qrCategory and hide = 0";
            NativeQuery<QrShop> query = session.createNativeQuery(sql, QrShop.class);
            query.setParameter("qrCategory", category);
            QrShop qr = query.uniqueResult();
            tr.commit();
            return qr;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    public Boolean saveQrCategory(QrShop qr) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(qr);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
        return true;
    }
}
