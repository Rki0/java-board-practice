package com.idontknow.board.repository;

import com.idontknow.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> { // JpaRepository<엔티티 타입, 엔티티 타입이 가지고 있는 PK 자료형>

    @Modifying // update 혹은 delete 작업에 붙이는 어노테이션
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id") // DB에 사용하는 쿼리를 작성할 수도 있고, Entity에 사용되는 쿼리를 작성할 수도 있다. 자유! 설정값이 따로 없으면 Entity에 사용되는 쿼리로 작성됨 // update board_table set board_hits=board_hits+1 where id=?
    void updateHits(@Param("id") Long id); // Param 어노테이션은 Query 어노테이션에 있는 :id 부분의 값을 가져오는 것이다.
}
