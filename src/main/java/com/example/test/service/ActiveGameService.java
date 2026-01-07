package com.example.test.service;

import com.example.test.entity.Game;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveGameService {
    private final Map<String, Long> userActiveGames = new ConcurrentHashMap<>();

    public void setActiveGame(String username, Long gameId) {
        userActiveGames.put(username, gameId);
    }

    public Long getActiveGame(String username) {
        return userActiveGames.get(username);
    }

    public void removeActiveGame(String username) {
        userActiveGames.remove(username);
    }
}

