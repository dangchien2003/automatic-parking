package com.automaticparking.model.shopQr;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

import java.util.List;

@Service
public class QrShopRepository {
    public List<QrShop> getAllCodeOk() {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            String sql = "SELECT * FROM shopqr WHERE hide = 0";
            NativeQuery<QrShop> query = session.createNativeQuery(sql, QrShop.class);
            List<QrShop> qr = query.list();
            tr.commit();
            session.close();
            return qr;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public QrShop getOneQrById(String category) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            String sql = "SELECT * From shopqr where qrCategory = :qrCategory and hide = 0";
            NativeQuery<QrShop> query = session.createNativeQuery(sql, QrShop.class);
            query.setParameter("qrCategory", category);
            QrShop qr = query.uniqueResult();
            tr.commit();
            session.close();
            return qr;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Boolean saveQrCategory(QrShop qr) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(qr);
            tr.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            session.close();
        }
        return true;
    }
}
