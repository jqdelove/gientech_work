package com.work.bitmap;

import redis.clients.jedis.Jedis;

/**
 * @description: 基于 Bitmap 实现 id 去重
 * @author: jinqi
 * @create: 2023-02-26 22:33
 **/
public class BitmapDemo {

    public static void main(String[] args) {
        // 连接Redis
        Jedis jedis = new Jedis("localhost", 6379);

        // 添加数据
        int[] ids = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int id : ids) {
            jedis.setbit("id_bitmap", id, true);
        }

        // 判断id是否存在
        System.out.println("id=1 是否存在：" + jedis.getbit("id_bitmap", 1)); // true
        System.out.println("id=11 是否存在：" + jedis.getbit("id_bitmap", 11)); // false

        // 统计存在的id数量
        long count = jedis.bitcount("id_bitmap");
        System.out.println("存在的id数量：" + count);

        // 关闭连接
        jedis.close();
    }
}
