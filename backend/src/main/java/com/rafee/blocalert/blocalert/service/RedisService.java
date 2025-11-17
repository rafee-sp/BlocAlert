package com.rafee.blocalert.blocalert.service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.Set;

public interface RedisService {


    void valueSet(String key, Object value);

    <T> T valueGet(String key, TypeReference<T> tClass);

    void hashSet(String key, String field, Object value);

    <T> T hashGet(String key, String field, Class<T> tClass);

    void hashPutAll(String key, Map<String, String> valueMap);

    Map<String, Map<Object, Object>> hashGetAll(Set<String> keys);

    void hashDelField(String key, String field);

    void hashDelFields(String key, Object[] fields);
}
