package com.automaticparking.model.customer;

import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import util.hibernateUtil;

@Repository
public class CustomerRepository {
    public Boolean saveCustomer(Customer dataCustomer) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.save(dataCustomer);
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

    public Customer getCustomerByEmail(String email) {
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
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public Customer getCustomerByUid(String uid) {
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
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            session.close();
        }
    }

    public Boolean updateCustomer(Customer customer) {
        Session session = hibernateUtil.openSession();
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.update(customer);
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
}
