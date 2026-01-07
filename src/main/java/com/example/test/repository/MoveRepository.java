package com.example.test.repository;

import com.example.test.entity.Game;
import com.example.test.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findByGameOrderByMoveNumberAsc(Game game);
    Move findTopByGameOrderByMoveNumberDesc(Game game);
}

