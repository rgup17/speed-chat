package com.speedchat.server.services;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedissonClient redissonClient;

    @Autowired
    public RedisService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void addDataToRedis(String mapName, String key, String value) {
        RMap<String, String> rMap = redissonClient.getMap(mapName);
        rMap.put(key, value);
        System.out.println("Data added to Redis: " + key + " -> " + value);
    }

    public String getDataFromRedis(String mapName, String key) {
        RMap<String, String> rMap = redissonClient.getMap(mapName);
        return rMap.get(key);
    }
}

