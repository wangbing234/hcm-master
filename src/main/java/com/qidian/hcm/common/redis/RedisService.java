package com.qidian.hcm.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置过期时间
     */
    public void expire(final String key, long expireTime) {
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存
     */
    public void set(final String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入缓存并设置时效时间
     */
    public void set(final String key, Object value, Long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 删除单个key
     */
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除key
     */
    public void batchRemove(String... keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 模糊删除key
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 判断缓存中是否有对应的key
     */
    public Boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void hmSet(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object hmGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }


    /**
     * 列表添加
     */
    public Long leftPush(String k, Object v) {
        return redisTemplate.opsForList().leftPush(k, v);
    }

    /**
     * 列表添加
     */
    public Long rightPush(String k, Object v) {
        return redisTemplate.opsForList().rightPush(k, v);
    }

    /**
     * 集合添加
     */
    public Long addSet(String key, Object value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 有序集合添加
     */
    public void zsetAdd(String key, Object value, double source) {
        redisTemplate.opsForZSet().add(key, value, source);
    }

    /**
     * 升序获取
     */
    public Set<Object> rangeByScore(String key, double source, double source1) {
        return redisTemplate.opsForZSet().rangeByScore(key, source, source1);
    }
}