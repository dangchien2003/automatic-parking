package com.automaticparking.middleware;

import com.automaticparking.Repositorys.StaffRepository;
import com.automaticparking.database.entity.Staff;
import com.automaticparking.exception.AuthorizedException;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import util.Cookies;
import util.Json;

import java.util.Objects;

@Component
@AllArgsConstructor
public class TokenStaff implements HandlerInterceptor {
    private StaffRepository staffRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie[] cookies = request.getCookies();
        Cookies CookieUtil = new Cookies(cookies);
        Cookie Stoken = CookieUtil.getCookieByName("SToken");
        if (Stoken == null) {
            throw new AuthorizedException("Invalid token");
//            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Not found token"), HttpStatus.BAD_REQUEST);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
//
//            response.setContentType("application/json");
//            response.getWriter().write(jsonResponse);
//            response.setStatus(errorResponse.getStatusCodeValue());
//            return false; // endpoint
        }

        String token = Stoken.getValue();
        Boolean error = false;
        if (token.trim() == "") {
            error = true;
        }

        Staff staffDataToken = new Staff();
        if (!error) {
            JWT<Staff> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(token);
            if (dataToken == null) {
                error = true;
            } else {
                Json<Staff> json = new Json<>();
                //  lấy dữ liệu token
                staffDataToken = json.jsonParse(dataToken.getSubject(), Staff.class);
            }
        }

        if (error || staffDataToken == null) {
            throw new AuthorizedException("Invalid token");
        }
        /*get info staff from DB*/
        Staff staffInfo = staffRepository.findByEmail(staffDataToken.getEmail()).orElseThrow();

        // kiểm tra tài khoản bị block
        if (staffInfo.getBlock() == 1) {
            throw new AuthorizedException("Account Blocked");
        }

        // kiểm tra phiên đăng nhập của tk
        if (!Objects.equals(staffInfo.getLastLogin(), staffDataToken.getLastLogin())) {
            throw new AuthorizedException("Login session ended");
        }

        request.setAttribute("staffDataToken", staffDataToken);
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {
    }
}
