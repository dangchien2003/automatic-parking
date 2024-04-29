package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.Cash;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import util.hibernateUtil;

@Service
public class CashCustomerService {
    public Boolean saveCashHistory(Cash cash) {
        Session session = hibernateUtil.openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(cash);
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
