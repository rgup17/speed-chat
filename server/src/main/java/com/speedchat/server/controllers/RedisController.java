package com.speedchat.server.controllers;

import com.speedchat.server.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    @Autowired
    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addData(@RequestParam String mapName, @RequestParam String key, @RequestParam String value) {
        redisService.addDataToRedis(mapName, key, value);
        return ResponseEntity.ok("Data added successfully.");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getData(@RequestParam String mapName, @RequestParam String key) {
        String value = redisService.getDataFromRedis(mapName, key);
        return ResponseEntity.ok("Value: " + value);
    }
}
