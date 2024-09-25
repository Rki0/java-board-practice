package com.idontknow.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "board_file_table")
public class BoardFileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY) // EAGER는 부모를 조회할 때 자식까지 가져옴. LAZY는 부모를 조회할 때 자식을 안 가져옴.
    @JoinColumn(name = "board_id") // DB에 만들어지는 컬럼명을 정한다.
    private BoardEntity boardEntity; // String, Long 이런게 아니라 부모 엔티티 타입을 적어줘야한다!!

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storedFileName) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();

        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(boardEntity); // PK(부모 데이터 Id 값)가 아니라 부모 엔티티를 넘겨줘야 한다.

        return boardFileEntity;
    }
}
