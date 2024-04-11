package encrypt;

import io.jsonwebtoken.Claims;
import util.Genarate;

import java.util.Map;


public class cs {
    public static void main(String[] args) {
        JWT<test1> jwt = new JWT<test1>();
        test1 a = new test1();

        String jwtString = null;
        try {
            jwtString = jwt.createJWT(a);
            System.out.println(jwtString);
        }catch ( Exception e) {
            System.out.println("Lỗi create jwt");
        }

        Claims claims = jwt.decodeJWT(jwtString);
        Map<String, String> data = Genarate.getMapFromJson(claims.getSubject());
        System.out.println(data.get("s1"));
    }


}