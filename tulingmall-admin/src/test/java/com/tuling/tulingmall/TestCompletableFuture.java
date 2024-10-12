package com.tuling.tulingmall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author WangJinbiao
 * @date 2024/10/12 22：43
 * 如果只是添加少量数据，HashMap 的线程安全问题可能不容易复现，因为并发冲突的概率较低。
 * 为了更容易复现问题，增加并发写入的数量和操作速度可以更清晰地看到 HashMap 在并发场景下的异常行为。
 *
增加并发操作：每个任务不再只添加一条数据，而是每个 CompletableFuture 向 HashMap 添加 1000 条数据，总共 5000 条数据。这样更容易触发并发冲突。

并发写入 HashMap：每个任务在同一时间对共享的 HashMap 进行大量写操作，更容易复现线程安全问题。

 */
public class TestCompletableFuture {


    public static void main(String[] args) {
        // 创建一个非线程安全的 HashMap,解决方法：使用 ConcurrentHashMap
        Map<Integer, String> unsafeMap = new HashMap<>();

        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        /**
         * 创建多个 CompletableFuture 并行执行任务，模拟高并发写入 HashMap
         * 增加并发操作：每个任务不再只添加一条数据，而是每个 CompletableFuture 向 HashMap 添加 1000 条数据，总共 5000 条数据。这样更容易触发并发冲突。
          */
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 1000; i++) {
                unsafeMap.put(i, "TaskResult from thread 1 - " + i);
            }
        }, executorService);

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            for (int i = 1000; i < 2000; i++) {
                unsafeMap.put(i, "TaskResult from thread 2 - " + i);
            }
        }, executorService);

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            for (int i = 2000; i < 3000; i++) {
                unsafeMap.put(i, "TaskResult from thread 3 - " + i);
            }
        }, executorService);

        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
            for (int i = 3000; i < 4000; i++) {
                unsafeMap.put(i, "TaskResult from thread 4 - " + i);
            }
        }, executorService);

        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {
            for (int i = 4000; i < 5000; i++) {
                unsafeMap.put(i, "TaskResult from thread 5 - " + i);
            }
        }, executorService);

        // 等待所有任务完成
        CompletableFuture.allOf(future1, future2, future3, future4, future5).join();

        /**
         * 输出 HashMap 的大小，检查是否有数据丢失
         * 期望的输出：5000,
         * 由于多个线程同时修改 HashMap，你可能会看到以下问题：
         * HashMap 的大小小于预期的 5000，因为部分数据被覆盖或丢失。
         * 打印的数据可能不完整，或任务结果可能被其他线程的数据覆盖。
         */
        System.out.println("HashMap size: " + unsafeMap.size());

        // 输出 HashMap 中的部分内容以验证数据
        unsafeMap.forEach((key, value) -> {
             System.out.println("Key: " + key + ", Value: " + value);
        });

        // 关闭线程池
        executorService.shutdown();
    }
}
