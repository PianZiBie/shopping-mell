package com.example.mell.search.thread;

import javax.management.remote.rmi._RMIConnection_Stub;
import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.*;

public class ThreadTest {
    public static void main(String[] args) throws Exception {
//        Thread thread01 = new Thread01();
//
//
//        thread01.start();
//
//        Runable01 runable01 = new Runable01();
//        new Thread(runable01).start();
//        FutureTask<Integer> integerFutureTask = new FutureTask<>(new callable());


//        Thread thread = new Thread(integerFutureTask);
//        thread.start();
//        Integer integer = integerFutureTask.get(); //等待线程执行完才可以得到返回值
//        System.out.println(integer);
/**
 * 七大参数
 * carePoolSize：核心线程数（一直存在除非【allowCoreThreadTimout】）；线程池，创建后一直存在,并且准备工作
 * maximumPoolSize:最大线程数量：控制资源
 * keepAliveTime:存活时间；如果当前的线程数量大于核心线程数并且大于存活时间，会释放空闲线程maximumPoolSize-carePoolSize
 * unit:时间单位
 * BlockingQueue<Runnable>: workQueue:阻塞队列。如果任务有很多，就会将目前多的任务放到队列里面
 *                          只有有线程空闲，就会去队列粒取出新的任务继续执行
 * threadFactory：线程的创建工程
 * handler:如果队列满了，按照我们指定的拒绝策略拒绝执行任务
 * rejectedExecutionHandler：
 *
 *1、线程池创建，准备好 core 数量的核心线程，准备接受任务
 * 2、新的任务进来，用 core 准备好的空闲线程执行。
 * (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队
 * 列获取任务执行
 * (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
 * (3) 、max 都执行好了。Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自
 * 动销毁。最终保持到 core 大              小
 * (4) 、如果线程数开到了 max 的数量，还有新任务进来，就会使用 reject 指定的拒绝策
 * 略进行处理
 * 3、所有的线程创建都是由指定的 factory
 *
 *
 */
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
//        //
//        //Executors.newCachedThreadPool();core是零，所有都可以回收
//        //Executors.newFixedThreadPool(30);core=max,所有都不可以回收
//        //Executors.newScheduledThreadPool();定时任务的线程池
//        //Executors.newSingleThreadExecutor()单线程的线程池，后台从队列里面过去任务挨个执行
//
//
//        System.out.println("执行了main方法");

        ExecutorService executorService = Executors.newFixedThreadPool(10);

//        CompletableFuture<Void> future = CompletableFuture.runAsync(
//                () -> {
//                    System.out.println("线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("目前数字为：" + i);
//                }
//
//                , executorService);
//        CompletableFuture<Void> future1 = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("目前数字为：" + i);
//                    return i;
//                }
//                , executorService).thenRunAsync(
//                () -> {
//                    System.out.println("任务二启动了");
//                }, executorService
//        );
//        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("目前数字为：" + i);
//                    return i;
//                }
//                , executorService).thenApplyAsync(
//                res -> {
//                    System.out.println("任务四启动了..." + res);
//                    return "Hello" + res;
//                }
//                , executorService
//        );
//        CompletableFuture<Void> future2 = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("目前数字为：" + i);
//                    return i;
//                }
//                , executorService).thenAcceptAsync(
//                res -> {
//                    System.out.println("任务三启动了..."+res);
//                },executorService
//        );
//        CompletableFuture<String> stringCompletableFuture1 = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("目前数字为：" + i);
//                    return i;
//                }
//                , executorService).thenApplyAsync(
//                res -> {
//                    System.out.println("任务四启动了..." + res);
//                    return "Hello" + res;
//                }
//                , executorService
//        );
//        CompletableFuture<Integer> Future01 = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("任务一线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//                    System.out.println("任务一结束");
//                    return i;
//                }
//                , executorService);
//        CompletableFuture<String> Future02 = CompletableFuture.supplyAsync(
//                () -> {
//                    System.out.println("任务二线程是：" + Thread.currentThread().getId());
//                    int i = 5;
//
//
//                    try {
////                        Thread.sleep(1000);
//                        System.out.println("任务二结束");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    return "Hello";
//                }
//                , executorService);
//        Future01.runAfterBothAsync(Future02, () -> {
//            System.out.println("任务三线程是：" + Thread.currentThread().getId());
//            System.out.println("任务三结束");
//        },executorService);
//        Future02.thenAcceptBothAsync(Future01,
//                (f1, f2) -> {
//                    System.out.println("f1"+f1);
//                    System.out.println("f2"+f2);
//                },executorService
//                );
//        CompletableFuture<String> eFuture03 = Future01.thenCombineAsync(Future02, (f1, f2) -> {
//            return f1 + "= " + f2 + " -haha";
//        }, executorService);
//        System.out.println(eFuture03.get());
//
//        System.out.println("main方法结束");

//        Future01.runAfterEitherAsync(Future02, () -> {
//            System.out.println("任务三线程是：" + Thread.currentThread().getId());
//        },executorService);


//        Future02.applyToEitherAsync(Future01, res -> {
//            System.out.println("任务三线程是：" + Thread.currentThread().getId());
//            return res.toString();
//        },executorService);


        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("查询图片信息");
                    return "Hello.jpg";
                },executorService
        );
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("查询商品的属性");
                    return "黑色+256G";
                },executorService
        );
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("查询商品的介绍");
                    return "华为";
                },executorService
        );
    //        CompletableFuture<Void> future = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
    //        future.get();
        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        System.out.println(objectCompletableFuture.get());

        System.out.println("main方法结束");

        
    }

    public static class Thread01 extends Thread {
        @Override
        public synchronized void run() {
            int i = 0;
            System.out.println("线程是：" + Thread.currentThread().getId());

            System.out.println("目前数字为：" + i);
        }
    }

    public static class Runable01 implements Runnable {
        @Override
        public synchronized void run() {
            int i = 2;
            System.out.println("线程是：" + Thread.currentThread().getId());

            System.out.println("目前数字为：" + i);
        }
    }

    public static class callable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            Integer i = 5;
            System.out.println("线程是：" + Thread.currentThread().getId());

            System.out.println("目前数字为：" + i);
            return i;
        }
    }
}
