package com.example.mell.product;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.UUID;

@SpringBootTest
public class RedisTest {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedissonClient redissonClient;
    @Test
    public void test(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","word:"+ UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println("保存的数据："+hello);
    }
    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }
}
