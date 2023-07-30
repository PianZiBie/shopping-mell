package com.example.mell.authserver.controller;

import com.example.common.constant.AuthServerConstant;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import com.example.mell.authserver.feign.ThirdPartFeignService;
import com.example.mell.authserver.vo.UserRegistVo;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Resource
    ThirdPartFeignService thirdPartFeignService;
    @Resource
    StringRedisTemplate redisTemPlate;
    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone){
        //TODO:接口防刷
        phone="844840392@qq.com";

        String code = UUID.randomUUID().toString().substring(0,5)+"-"+System.currentTimeMillis();


        String redisCode = redisTemPlate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotEmpty(redisCode)){
        long time = Long.parseLong(redisCode.split("-")[1]);

        if (System.currentTimeMillis()-time<60000){
            return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
        }
        }


        thirdPartFeignService.sendCode(phone,code);
        //redis缓存，//防止同一个phone在60秒内再发发送验证码
        redisTemPlate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code,10,TimeUnit.MINUTES);

        return R.ok();
    }
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result){
        if (result.hasErrors()){
            //校验出错，转发到注册页
            return "forward://reg";
        }
    //注册成功回到首页，回到登录页
    return null;
    }
}
