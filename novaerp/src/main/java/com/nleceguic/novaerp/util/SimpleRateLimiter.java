package com.nleceguic.novaerp.util;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleRateLimiter {
    private final ConcurrentHashMap<String, List<Instant>> map = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final Duration window;

    public SimpleRateLimiter(int maxRequests, Duration window) {
        this.maxRequests = maxRequests;
        this.window = window;
    }

    public synchronized boolean isAllowed(String key) {
        List<Instant> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        Instant now = Instant.now();

        list.removeIf(t -> t.isBefore(now.minus(window)));
        if (list.size() >= maxRequests) {
            return false;
        } else {
            list.add(now);
            return true;
        }
    }
}
