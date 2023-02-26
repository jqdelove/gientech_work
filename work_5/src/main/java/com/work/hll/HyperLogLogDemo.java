package com.work.hll;

import redis.clients.jedis.Jedis;

/**
 * @description: 基于 HLL 实现点击量计数13
 * @author: jinqi
 * @create: 2023-02-26 22:34
 **/
public class HyperLogLogDemo {

    public static void main(String[] args) {
        // 连接Redis
        Jedis jedis = new Jedis("localhost", 6379);

        // 添加点击数据
        String[] clicks = {"user1", "user2", "user3", "user1", "user2", "user4"};
        for (String click : clicks) {
            jedis.pfadd("clicks", click);
        }

        // 获取点击量估计值
        long count = jedis.pfcount("clicks");
        System.out.println("点击量估计值：" + count);

        // 关闭连接
        jedis.close();
    }
}
