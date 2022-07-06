package com.zcx.ymyy.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.zcx.ymyy.exception.TokenUnavailable;
import com.zcx.ymyy.entity.Result;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.Admin;
import com.zcx.ymyy.service.AdminService;
import com.zcx.ymyy.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 管理员拦截器
 */
public class AdminJwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        String token = httpServletRequest.getHeader("x-token");
        // 拦截的不是方法，直接放行
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        // 该方法是否有@PassToken注解
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            // required为true表示不验证token，为false表示验证token
            if (passToken.required()) {
                return true;
            }
        }

        if (token == null) {
            Result result = Result.error("验证失败，token为空");
            toOutJSON(result, httpServletResponse);
            return false;
        }


        String userType = JwtUtils.getClaimByName(token,"userType").asString();
        if (!"admin".equals(userType)) {
            Result result = Result.error("验证失败，用户类型不匹配");
            toOutJSON(result, httpServletResponse);
            return false;
        }

        String adminId = JwtUtils.getAudience(token);
        Admin admin = adminService.getById(Integer.parseInt(adminId));
        if (admin == null) {
            Result result = Result.error("验证失败，用户不存在");
            toOutJSON(result, httpServletResponse);
            return false;
        }

        try {
            JwtUtils.verifyToken(token, adminId);
        } catch (TokenUnavailable e) {
            Result result = Result.error("验证失败，token有误");
            toOutJSON(result, httpServletResponse);
            return false;
        }

        return true;

    }

    private void toOutJSON(Result result, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(JSONObject.toJSONString(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }


}
