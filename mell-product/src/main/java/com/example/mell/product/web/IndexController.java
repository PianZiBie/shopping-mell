package com.example.mell.product.web;

import com.example.mell.product.controller.CategoryController;
import com.example.mell.product.entity.CategoryEntity;
import com.example.mell.product.service.CategoryService;
import com.example.mell.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Resource
    CategoryService categoryService;
    @Resource
    RedissonClient redissonClient;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntities= categoryService.getLevelCategorys();
        model.addAttribute("categories",categoryEntities);
        return "index";
    }
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;

    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        RLock lock = redissonClient.getLock("my-lock");
        //加锁 阻塞性等待
        lock.lock();
        //lock.lock(10, TimeUnit.SECONDS);10自动解锁没有看门狗机制
        //如果我们传递了锁的超市时间 就发送给redis执行脚本
        try{
            System.out.println("写锁加锁成功，执行业务"+Thread.currentThread().getId());
            Thread.sleep(30000);
        }
        catch (Exception e){

        }finally {
            System.out.println("释放写锁"+Thread.currentThread().getId());
            //释放锁
            lock.unlock();
        }

        return "hello";
    }

    /**”
     * 读读 相当于无锁
     * 只要有写都必须等待 例如读写 先读再写
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){
        String string = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try{
            //加写锁



            string = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeValue",string);
            System.out.println("写锁加锁成功"+Thread.currentThread().getId());

            Thread.sleep(20000);
        }
        catch (Exception e){

        }finally {
            System.out.println("释放写锁"+Thread.currentThread().getId());
            //释放锁
            rLock.unlock();

        }
        return null;
    }
    @ResponseBody
    @GetMapping("/read")
    public String readValue(){
        String string = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try{

            string = stringRedisTemplate.opsForValue().get("writeValue");
            System.out.println("读锁加锁成功"+Thread.currentThread().getId());
            Thread.sleep(30000);

        }
        catch (Exception e){

        }finally {
            System.out.println("释放读锁"+Thread.currentThread().getId());
            //释放锁
        rLock.unlock();
        }
        return string;
    }
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");

        boolean b = park.tryAcquire();
        return "ok"+b;
    }
    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "go -ok";
    }
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
                door.trySetCount(5);
                door.await();

        return "放假了";

    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String lockDoor(@PathVariable("id") Long id){

        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();

        return id+"班的人走了";

    }

}
