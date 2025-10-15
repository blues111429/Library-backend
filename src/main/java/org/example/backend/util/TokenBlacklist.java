package org.example.backend.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

//token黑名单
public class TokenBlacklist {

    //创建黑名单缓存
    private static final Cache<String, Boolean> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    //加入黑名单
    public static void add(String token) {
        blacklist.put(token, Boolean.TRUE);
    }

    //检查是否在黑名单
    public static boolean isBlacklisted(String token) {
        return blacklist.getIfPresent(token) != null;
    }
}
