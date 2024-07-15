package com.automaticparking.model.customer;

import util.CustomRandom;
import util.Genarate;

public class Util {
    public static String genarateUid() {
        return "CUSTOMER_" + Genarate.getTimeStamp() + "_" + Genarate.randomLetters(3);
    }
}
