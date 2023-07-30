package com.example.melll.thirdparty.controller;

import com.example.common.utils.R;
import com.example.melll.thirdparty.component.MailComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sms")
public class MailSendController {
    @Resource
    MailComponent mailComponent;

    /**
     * 提供给别的服务调用
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        long l1 = System.currentTimeMillis();
        mailComponent.sendSimpleMail(phone, "验证码", code);
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1);
        return R.ok();
    }
}
