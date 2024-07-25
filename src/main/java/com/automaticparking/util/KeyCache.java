package com.automaticparking.util;

public class KeyCache {
    public static String getKeyContentQr(String uid, String qrid) {
        return "CQR-" + uid + "-" + qrid;
    }
}
