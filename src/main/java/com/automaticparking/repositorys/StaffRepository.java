package com.automaticparking.repositorys;

import com.automaticparking.database.entity.Staff;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StaffRepository {
    public Boolean createStaff(Staff staff) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(staff);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
        return true;
    }

    public List<Staff> getAllStaff() throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE admin != 1";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            List<Staff> staff = query.list();
            tr.commit();
            return staff;
        } catch (Exception e) {
            e.printStackTrace();
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public Staff getOneStaffByEmail(String email) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE email = :email";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            query.setParameter("email", email);
            Staff staff = query.uniqueResult();
            tr.commit();
            return staff;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public BigInteger countAdmin() {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT COUNT(*) FROM staff WHERE admin = :isAdmin";
            NativeQuery<BigInteger> query = session.createNativeQuery(sql);
            query.setParameter("isAdmin", 1);
            BigInteger countAdmin = (BigInteger) query.uniqueResult();
            tr.commit();
            return countAdmin;
        } catch (Exception e) {
            e.printStackTrace();
            tr.rollback();
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public Staff getOneStaffBySid(String sid) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE sid = :sid";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            query.setParameter("sid", sid);
            Staff staff = query.uniqueResult();
            tr.commit();
            return staff;
        } catch (Exception e) {
            e.printStackTrace();
            tr.rollback();
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public Boolean updateStaff(Staff staff) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.update(staff);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
        return true;

    }

    public List<Staff> getListStaffByEmailAndSid(String sid, String email) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM staff WHERE sid = :sid or email = :email";
            NativeQuery<Staff> query = session.createNativeQuery(sql, Staff.class);
            query.setParameter("sid", sid);
            query.setParameter("email", email);
            List<Staff> staff = query.list();
            tr.commit();
            return staff;
        } catch (Exception e) {
            e.printStackTrace();
            tr.rollback();
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

}
