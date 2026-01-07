package com.example.test.service;

import com.example.test.entity.Game;
import com.example.test.entity.User;
import com.example.test.repository.GameRepository;
import com.example.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public Game createGame(String player1Username, String player2Username) {
        User player1 = userRepository.findByUsername(player1Username)
                .orElseThrow(() -> new RuntimeException("Player 1 not found"));
        User player2 = userRepository.findByUsername(player2Username)
                .orElseThrow(() -> new RuntimeException("Player 2 not found"));

        Game game = new Game(player1, player2);
        return gameRepository.save(game);
    }

    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }
}

