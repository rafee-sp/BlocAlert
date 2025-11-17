package com.rafee.blocalert.blocalert.utils;

import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;

public final class RedisKeys {

    private RedisKeys() {}

    public static String CRYPTO_FULL_HASH = "crypto:data:full";
    public static String CRYPTO_DATA_LITE = "crypto:data:lite";
    public static String MARKET_STATS = "market:stats";

    public static String alertKey(String cryptoId) {
        return "alerts:coin:"+cryptoId;
    }
    public static String alertId(Long alertId) {
        return "alert:"+alertId;
    }

    public static String templateKey(AlertChannel channel, String code) {
        return String.join(":", "template", code.toLowerCase(), channel.name().toLowerCase());
    }

}
