package cn.bitflash.interceptor;

import cn.bitflash.annotation.Login;
import cn.bitflash.exception.RRException;
import cn.bitflash.feignInterface.UserLoginFeign;
import cn.bitflash.login.TokenEntity;
import cn.bitflash.redisConfig.RedisKey;
import cn.bitflash.utils.AESTokenUtil;
import cn.bitflash.utils.RedisUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ApiLoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserLoginFeign userLoginFeign;

    private final String MOBILE = "mobile";
    private final String UID = "uid";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        } else {
            return true;
        }

        if (annotation == null) {
            return true;
        }
        String mobile = request.getHeader(MOBILE);
        if (mobile.length() == 0 || mobile == null) {
            mobile = request.getParameter(MOBILE);
        }
        String secretUid = request.getParameter(UID);
        if (secretUid.length() == 0 || secretUid == null) {
            secretUid = request.getParameter(UID);
        }
        if (mobile == null || secretUid == null) {
            throw new RRException("参数不能为空");
        }
        String token = redisUtils.get(RedisKey.LOGIN_ + mobile);

        TokenEntity tokenEntity = userLoginFeign.selectOneByToken(new EntityWrapper<TokenEntity>().eq("token",token));

        if (tokenEntity == null) {
            throw new RRException("token信息错误");
        }

        if (tokenEntity.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new RRException("toke过期，请重新登录！");
        }

        String uid = AESTokenUtil.getData(token, secretUid);
        if (!tokenEntity.getUid().equals(uid)) {
            throw new RRException("token信息与用户信息不符");
        }
        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(UID, tokenEntity.getUid());
        return true;

    }
}
