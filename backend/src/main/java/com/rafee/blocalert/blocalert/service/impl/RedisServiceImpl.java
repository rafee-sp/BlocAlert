package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void valueSet(String key, Object value) {
        try {

            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);

        } catch (Exception e) {
            log.error("Failed to serialize value", e);
        }
    }

    @Override
    public <T> T valueGet(String key, TypeReference<T> typeRef) {

        try {

            String json = redisTemplate.opsForValue().get(key);
            return json != null ? objectMapper.readValue(json, typeRef) : null;

        } catch (Exception e) {
            log.error("Failed to deserialize value", e);
            return null;
        }
    }

    @Override
    public void hashSet(String key, String field, Object value) {

        try {

            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForHash().put(key, field, json);

        } catch (Exception e) {
            log.error("Failed to serialize hash value", e);
        }

    }

    @Override
    public <T> T hashGet(String key, String field, Class<T> tClass) {

        try {

            String json = (String) redisTemplate.opsForHash().get(key, field);
            return json != null ? objectMapper.readValue(json, tClass) : null;

        } catch (Exception e) {
            log.error("Failed to deserialize hash value", e);
            return null;
        }
    }

    @Override
    public void hashPutAll(String key, Map<String, String> valueMap) {
        try {
            redisTemplate.opsForHash().putAll(key, valueMap);
        } catch (Exception e) {
            log.error("Failed to serialize hashPutAll", e);
        }
    }

    @Override
    public Map<String, Map<Object, Object>> hashGetAll(Set<String> keys) {

        Map<String, Map<Object, Object>> resultsMap = new LinkedHashMap<>();

        try {

            List<Object> pipelinedResults = redisTemplate.executePipelined(
                    (RedisCallback<Object>) connection -> {
                        keys.forEach(key -> {
                            connection.hashCommands().hGetAll(key.getBytes());
                        });
                        return null;
                    }
            );

            Iterator<String> keyIterator = keys.iterator();
            for (Object result : pipelinedResults) {

                String key = keyIterator.next();
                if (result instanceof Map) {
                    resultsMap.put(key, (Map<Object, Object>) result);
                } else {
                    resultsMap.put(key, Collections.emptyMap());
                }

            }

        } catch (Exception e) {
            log.error("Failed to get all hash");
        }

        return resultsMap;
    }

    @Override
    public void hashDelField(String hashKey, String field) {
        try {
            redisTemplate.opsForHash().delete(hashKey, field);
        } catch (Exception e) {
            log.error("Failed to delete field from hash {} , field {}", hashKey, field);
        }
    }

    @Override
    public void hashDelFields(String key, Object[] fields) {

        try {
            redisTemplate.opsForHash().delete(key, fields);
        } catch (Exception e) {
            log.error("Failed to delete hash fields from Redis for key : {}, error {}", key, e.getMessage(), e);
        }
    }

}
