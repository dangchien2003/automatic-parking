package util;


import jakarta.servlet.http.Cookie;

public class Cookies {
    private Cookie[] cookies;
    public Cookies(Cookie[] cookies) {

    }
    public Cookie getCookieByName(String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
