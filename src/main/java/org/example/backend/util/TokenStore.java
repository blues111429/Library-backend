package org.example.backend.util;

import java.util.concurrent.ConcurrentHashMap;

//token的存取以及移除
public class TokenStore {
    private static final ConcurrentHashMap<Integer, String> userTokens = new ConcurrentHashMap<>();

    public static void save(Integer userId, String token) {
        userTokens.put(userId, token);
    }

    public static String get(Integer userId) {
        return userTokens.get(userId);
    }

    public static void remove(Integer userId) {
        userTokens.remove(userId);
    }
}
