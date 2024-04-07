package com.automaticparking.model.staff;

import util.CustomDotENV;
import util.Encrypt;
import util.Genarate;
import util.CustomRandom;


public class Util {
    public static String genarateSid() {
        return "STAFF_" + Genarate.getTimeStamp() + "_" + CustomRandom.randomLetters(3);
    }

    public static Staff getDefaultStaff() {
        Staff staff = new Staff();
        staff.setBlock(0);
        staff.setCreateAt(Genarate.getTimeStamp());
        staff.setSid(Util.genarateSid());
        return staff;
    }

    public static Staff setStaff(Staff staff, Integer Admin, String name, String birthday, String email ) {
        staff.setAdmin(Admin);
        staff.setName(name);
        staff.setBirthday(birthday);
        staff.setEmail(email);
        staff.setPassword(new Encrypt().hash(CustomDotENV.get("PASSWORD_STAFF")));
        return staff;
    }

}
