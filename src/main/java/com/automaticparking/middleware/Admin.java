package com.automaticparking.middleware;

import com.automaticparking.database.entity.Staff;
import com.automaticparking.exception.AuthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class Admin  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");
        if (staffDataToken.getAdmin() == 1) {
            return true;
        }
        throw new AuthorizedException("Not have access");
//        ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(error(HttpStatus.UNAUTHORIZED, "Not have access"), HttpStatus.UNAUTHORIZED);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
//        response.setContentType("application/json");
//        response.getWriter().write(jsonResponse);
//        response.setStatus(errorResponse.getStatusCodeValue());
//        return false; // endpoint
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
