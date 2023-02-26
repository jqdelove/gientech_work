package com.work.rank;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * @description: 使用Redis的Java客户端Jedis实现简单的排行榜
 * @author: jinqi
 * @create: 2023-02-26 22:30
 **/
public class RankingDemo {

    public static void main(String[] args) {
        // 连接Redis
        Jedis jedis = new Jedis("localhost", 6379);

        // 添加数据到有序集合中
        jedis.zadd("score_ranking", 100, "user1");
        jedis.zadd("score_ranking", 200, "user2");
        jedis.zadd("score_ranking", 300, "user3");
        jedis.zadd("score_ranking", 400, "user4");

        // 获取排名前3的用户
        Set<Tuple> topUsers = jedis.zrevrangeWithScores("score_ranking", 0, 2);
        int rank = 1;
        for (Tuple tuple : topUsers) {
            System.out.println(rank + ". " + tuple.getElement() + " - " + tuple.getScore());
            rank++;
        }

        // 关闭连接
        jedis.close();
    }

}
