package com.idontknow.board.repository;

import com.idontknow.board.entity.BoardEntity;
import com.idontknow.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // Native Query : SELECT * FROM comment_table WHERE board_id-? ORDER BY id DESC;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
