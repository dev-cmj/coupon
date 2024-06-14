package com.cmj.couponapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread.sleep(500);
        return "Hello, World!";
    } // 초당 2건을 처리 * N (200) = 400 (서버에서 동시에 처리할 수 있는 수) = TPS (Transaction Per Second)
}
