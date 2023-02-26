package com.work.id;

/**
 * @description: 使用Snowflake算法生成全局唯一ID
 * @author: jinqi
 * @create: 2023-02-26 22:31
 **/

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 雪花算法ID生成器
 */
public class SnowflakeIdGenerator {
    private final long workerId; // 工作ID
    private final long epoch = 1420041600000L; // 起始时间戳，2015-01-01
    private final long workerIdBits = 5L; // 工作ID占用位数
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits); // 工作ID最大值
    private final long sequenceBits = 12L; // 序列号占用位数
    private final long workerIdShift = sequenceBits; // 工作ID位移量
    private final long timestampLeftShift = sequenceBits + workerIdBits; // 时间戳位移量
    private final long sequenceMask = -1L ^ (-1L << sequenceBits); // 序列号掩码
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    /**
     * 构造函数
     *
     * @param workerId 工作ID
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须在0和 " + maxWorkerId);
        }
        this.workerId = workerId;
    }

    /**
     * 生成下一个唯一ID
     *
     * @return 下一个唯一ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("停止生成ID。");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 等待下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 新的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        // 创建ID生成器
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);

        // 创建多线程执行器
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 测试生成10000个ID
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                long id = idGenerator.nextId();
                synchronized (ids) {
                    if (ids.contains(id)) {
                        System.out.println("发现重复ID：" + id);
                    } else {
                        ids.add(id);
                    }
                }
            });
        }

        // 关闭执行器
        executorService.shutdown();

        // 等待所有任务执行完成
        while (!executorService.isTerminated()) {
        }

        // 输出生成的ID数量
        System.out.println("生成的ID数量：" + ids.size());
    }

}

