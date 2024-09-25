package com.idontknow.board.controller;

import com.idontknow.board.dto.CommentDTO;
import com.idontknow.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    // ResponseEntity는 body와 header를 같이 담을 수 있는 객체이다!
    @PostMapping("/save")
    public ResponseEntity save(@ModelAttribute CommentDTO commentDTO) {
        System.out.println("commentDTO" + commentDTO);

        Long saveResult = commentService.save(commentDTO);

        if (saveResult != null) {
            // 댓글 작성 성공
            // 성공으로 끝나는게 아니라, 기존 댓글 목록에 새로운 댓글을 추가한 목록을 제공해줘야함!
            // 따라서 댓글 목록을 가져와서 return
            // 댓글 목록 : 해당 게시글의 댓글 전체 -> 해당 게시글 id를 기준으로 가져와야한다!
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
