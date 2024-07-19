package com.automaticparking.repositorys;

import com.automaticparking.database.entity.Customer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

import java.sql.SQLException;

@Repository
public class CustomerRepository {
    public Boolean saveCustomer(Customer dataCustomer) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(dataCustomer);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
        return true;
    }

    public Customer getCustomerByEmail(String email) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM user WHERE email = :email";
            NativeQuery<Customer> query = session.createNativeQuery(sql, Customer.class);
            query.setParameter("email", email);
            Customer customer = query.uniqueResult();
            tr.commit();
            return customer;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public Customer getCustomerByUid(String uid) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();

            String sql = "SELECT * FROM user WHERE uid = :uid";
            NativeQuery<Customer> query = session.createNativeQuery(sql, Customer.class);
            query.setParameter("uid", uid);
            Customer customer = query.uniqueResult();
            tr.commit();
            return customer;
        } catch (Exception e) {
            tr.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public Boolean updateCustomer(Customer customer) throws SQLException {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.update(customer);
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
