package com.automaticparking.model.code;

import com.automaticparking.model.shopQr.QrShop;
import util.CustomRandom;
import util.Genarate;

public class Util {
    public String genarateQrId() {
        return "CODE" + Genarate.getTimeStamp() + "_" + CustomRandom.randomLetters(4);
    }

    public Code setCode(String uid, QrShop qr, Integer discount) {
        Code code = new Code();
        Long now = Genarate.getTimeStamp();
        code.setQrid(genarateQrId());
        code.setQrCategory(qr.getQrCategory());
        code.setUid(uid);
        code.setBuyAt(now);
        code.setPrice(qr.getPrice()-discount);
        code.setExpireAt(now + qr.getMaxAge());
        return code;
    }

}
