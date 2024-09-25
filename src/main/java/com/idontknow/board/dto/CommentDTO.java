package com.idontknow.board.dto;

import com.idontknow.board.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    private Long id;
    private String commentWriter;
    private String commentContents;
    private Long boardId;
    private LocalDateTime commentCreatedTime;

    public static CommentDTO toCommentDTO(CommentEntity commentEntity, Long boardId) {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        commentDTO.setCommentCreatedTime(commentEntity.getCreatedTime());
//        commentDTO.setBoardId(commentEntity.getBoardEntity().getId()); // 이렇게 해도 되고, 어차피 boardId를 받아야만 하는 구조였으니까, 파라미터로 받아와도 되고. 이 방법을 쓴다면 Service 메서드에 @Transactional 어노테이션을 붙여줘야함.
        commentDTO.setBoardId(boardId);

        return commentDTO;
    }
}
