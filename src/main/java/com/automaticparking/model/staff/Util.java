package com.automaticparking.model.staff;

import util.DotENV;
import encrypt.Hash;
import util.Genarate;
import util.CustomRandom;


public class Util {
    public static String genarateSid() {
        return "STAFF_" + Genarate.getTimeStamp() + "_" + CustomRandom.randomLetters(3);
    }

    public static Staff getDefaultStaff() {
        Staff staff = new Staff();
        Long nowTimeStamp = Genarate.getTimeStamp();
        staff.setBlock(0);
        staff.setCreateAt(nowTimeStamp);
        staff.setSid(Util.genarateSid());
        staff.setLastLogin(nowTimeStamp);
        return staff;
    }

    public static Staff setStaff(Staff staff, Integer Admin, String name, String birthday, String email ) {
        staff.setAdmin(Admin);
        staff.setName(name);
        staff.setBirthday(birthday);
        staff.setEmail(email);
        staff.setPassword(new Hash().hash(DotENV.get("PASSWORD_STAFF")));
        return staff;
    }

}
