package com.automaticparking.model.staff;

import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import util.hibernateUtil;

import java.math.BigInteger;
import java.util.List;

public class StaffService {
    public Boolean createStaff(Staff staff) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(staff);
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

    public List<Staff> getAllStaff() {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE admin != 1";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            List<Staff> staff = query.list();
            tr.commit();
            session.close();
            return staff;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Staff getOneStaffByEmail(String email) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE email = :email";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            query.setParameter("email", email);
            Staff staff = query.uniqueResult();
            tr.commit();
            session.close();
            return staff;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public BigInteger countAdmin() {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT COUNT(*) FROM staff WHERE admin = :isAdmin";
            NativeQuery<BigInteger> query = session.createNativeQuery(sql);
            query.setParameter("isAdmin", 1);
            BigInteger countAdmin = (BigInteger) query.uniqueResult();
            tr.commit();
            session.close();
            return countAdmin;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Staff getOneStaffBySid(String sid) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE sid = :sid";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            query.setParameter("sid", sid);
            Staff staff = query.uniqueResult();
            tr.commit();
            session.close();
            return staff;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Boolean updateStaff(Staff staff) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.update(staff);
            tr.commit();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        finally {
            session.close();
        }
        return true;

    }

}
