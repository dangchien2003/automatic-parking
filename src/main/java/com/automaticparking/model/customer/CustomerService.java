package com.automaticparking.model.customer;

import org.hibernate.Session;
import org.hibernate.Transaction;
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
}
