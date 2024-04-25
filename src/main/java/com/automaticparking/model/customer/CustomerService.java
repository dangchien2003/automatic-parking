package com.automaticparking.model.customer;

import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import util.hibernateUtil;

public class CustomerService {
    public Boolean saveCustomer(Customer dataCustomer) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(dataCustomer);
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

    public Customer getCustomerByEmail(String email) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();

            String sql = "SELECT * FROM user WHERE email = :email";
            NativeQuery<Customer> query = session.createNativeQuery(sql, Customer.class);
            query.setParameter("email", email);
            Customer customer = query.uniqueResult();
            tr.commit();
            session.close();
            return customer;
        }catch (Exception e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Boolean updateCustomer(Customer customer) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.update(customer);
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
