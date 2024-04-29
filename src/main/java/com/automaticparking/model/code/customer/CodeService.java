package com.automaticparking.model.code.customer;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;
import util.Genarate;
import util.hibernateUtil;

import java.util.List;

@Service
public class CodeService {
    List<Code> getAllCodeUse(String uid) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            String sql = "SELECT * FROM qr WHERE uid = :uid AND (cancleAt IS NULL AND expireAt > :now) or (checkinAt IS NOT NULL)";
            NativeQuery<Code> query = session.createNativeQuery(sql, Code.class);
            query.setParameter("uid", uid);
            query.setParameter("now", Genarate.getTimeStamp());
            List<Code> codeUsed = query.list();
            tr.commit();
            session.close();
            return codeUsed;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    Boolean saveCode(Code code) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(code);
            tr.commit();
        }catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }finally {
            session.close();
        }
        return true;
    }
}
