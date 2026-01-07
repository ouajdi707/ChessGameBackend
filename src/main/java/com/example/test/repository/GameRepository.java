package com.example.test.repository;

import com.example.test.entity.Game;
import com.example.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByPlayer1OrPlayer2(User player1, User player2);
    List<Game> findByPlayer1OrPlayer2AndStatus(User player1, User player2, Game.GameStatus status);
    Optional<Game> findByIdAndPlayer1OrPlayer2(Long id, User player1, User player2);
}

