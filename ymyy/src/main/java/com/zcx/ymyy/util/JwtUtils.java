package com.zcx.ymyy.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.zcx.ymyy.exception.TokenUnavailable;

import java.util.Calendar;
import java.util.Date;

public class JwtUtils {

    /**
     签发对象：这个用户的id
     签发时间：现在
     有效时间：30分钟
     载荷内容：暂时设计为：这个人的用户名，这个人的昵称.这个人的用户类型
     加密密钥：这个人的id加上一串字符串
     */
    public static String createToken(String userId, String userName, String realName, String userType) {

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE,30);
        Date expiresDate = nowTime.getTime();

        return JWT.create().withAudience(userId)   //签发对象
                .withIssuedAt(new Date())    //发行时间
                .withExpiresAt(expiresDate)  //有效时间
                .withClaim("userName", userName)    //载荷，随便写几个都可以
                .withClaim("realName", realName)
                .withClaim("userType", userType)
                .sign(Algorithm.HMAC256(userId + "zcx666"));   //加密
    }

    /**
     * 检验合法性，其中secret参数就应该传入的是用户的id
     * @param token
     * @throws TokenUnavailable
     */
    public static void verifyToken(String token, String secret) throws TokenUnavailable {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret + "zcx666")).build();
            verifier.verify(token);
        } catch (Exception e) {
            throw new TokenUnavailable();
        }
    }

    /**
     * 获取签发对象
     * @param token
     * @return
     * @throws TokenUnavailable
     */
    public static String getAudience(String token) throws TokenUnavailable {
        String audience = null;
        try {
            audience = JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            throw new TokenUnavailable();
        }
        return audience;
    }


    /**
     * 通过载荷名字获取载荷的值
     * @param token
     * @param name
     * @return
     */
    public static Claim getClaimByName(String token, String name) throws TokenUnavailable {
        Claim claim = null;
        try {
            claim = JWT.decode(token).getClaim(name);
        } catch (Exception e) {
            throw new TokenUnavailable();
        }
        return claim;
    }

}
