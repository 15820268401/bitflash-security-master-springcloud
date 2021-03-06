package cn.bitflash.feign;

import cn.bitflash.login.TokenEntity;
import cn.bitflash.login.UserEntity;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bitflash-login")
public interface LoginFeign {

    @PostMapping("/api/login/withinToken/selectOne")
    TokenEntity selectOne(@RequestBody Wrapper<TokenEntity> entityWrapper);

    @PostMapping("/api/login/withinUser/selectOne")
    UserEntity selectOneByUser(@RequestBody Wrapper<UserEntity> entityWrapper);

    @PostMapping("/api/login/withinUser/update")
    boolean update(@RequestBody UserEntity userEntity,@RequestParam("entityWrapper") Wrapper<UserEntity> entityWrapper);

}
