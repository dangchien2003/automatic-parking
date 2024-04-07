package com.automaticparking.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import util.hibernateUtil;

public class abcService{
    public static void main(String[] args) {

        try {
            Session session = hibernateUtil.getSessionFactory().openSession();
            try {
                Transaction tr = session.beginTransaction();

                abcEntity abc1 = new abcEntity();
                hibernateUtil.AddEntity(abcEntity.class);
                abc1.setC1(3);
                abc1.setValue("chien1");

                session.update(abc1);
                tr.commit();
            }finally {
                session.close();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
