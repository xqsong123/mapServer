package com.map.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashMap;

public class JedisPoolUtils {

    private static JedisPool pool = null;

    static {
        //加载配置文件
        LinkedHashMap lhMap = new LinkedHashMap();
        try {
            lhMap = FileTools.inputFile("/redis.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获得池子对象
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(Integer.parseInt(lhMap.get("redis.maxIdle").toString()));//最大闲置个数
        poolConfig.setMinIdle(Integer.parseInt(lhMap.get("redis.minIdle").toString()));//最小闲置个数
        poolConfig.setMaxTotal(Integer.parseInt(lhMap.get("redis.maxTotal").toString()));//最大连接数
        //连接池url+port
        pool = new JedisPool(poolConfig, lhMap.get("redis.url").toString(), Integer.parseInt(lhMap.get("redis.port").toString()));
    }

    //获取jedis实例
    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void setPool(String key, String value){
        getJedis().set(key, value);
    }

    public static String getPoolValue(String key){
        return getJedis().get(key);
    }

    public static void releaseJedis(Jedis jedis){
        if(jedis != null){
            pool.returnResource(jedis);
        }
    }

    public static void clearBuffer(Jedis jedis){
        pool = JedisPoolUtils.pool;
        if(pool != null && jedis != null){
            pool.returnBrokenResource(jedis);
        }
    }
}
