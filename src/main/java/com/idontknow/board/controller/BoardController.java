package com.idontknow.board.controller;

import com.idontknow.board.dto.BoardDTO;
import com.idontknow.board.dto.CommentDTO;
import com.idontknow.board.service.BoardService;
import com.idontknow.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor // service 로직 끌어올 떄 필요. 즉, 외부에서 의존성 주입 받기 위해.
@RequestMapping("/board") // 이 컨트롤러에 있는 애들은 엔드 포인트가 /board/뭐시기 이렇게 되는 것임!
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

//    @PostMapping("/save")
//    public String save(@RequestParam("boardWriter") String boardWriter) { // RequestParam 어노테이션을 통해 req로 날아오는 값읊 파싱.
//        return null;
//    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);

        boardService.save(boardDTO);

        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) { // DB로부터 무엇인가를 가져올 때는 Model 객체를 사용한다.
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();

        model.addAttribute("boardList", boardDTOList); // boardDTOList를 model에 담아서 list.html로 넘겨주는 것

        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model, // url에 들어있는 정보를 얻기 위한 어노테이션
                           @PageableDefault(page=1) Pageable pageable) {
        /*
            게시물 조회는 두 가지를 고민해야함.
            1. 해당 게시글의 조회수를 하나 올리고
            2. 게시글 데이터를 가져와서 detail.html에 출력
         */

        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        // 댓글 목록 가져오기
        List<CommentDTO> commentDTOList = commentService.findAll(id);

        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());

        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";
//        return "redirect:/board/" + boardDTO.getId(); // 이 경우 조회수 영향이 있음
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
//        pageable.getPageNumber(); // ?page=1 의 형태일 때 사용 가능!! 여기서는 1을 가져온다.
        Page<BoardDTO> boardList = boardService.paging(pageable);

        // page 개수가 총 20개일 때,
        // 보여지는 페이지 개수 3개
        // 현재 사용자가 3 페이지를 보고 있다면
        // 1 2 3
        // 현재 사용자가 7 페이지에 있다면
        // 7 8 9
        // 이 때, 만약 총 페이지 개수가 8개라면
        // 7 8 까지만 보여줘야한다.

        // 보여지는 페이지 개수
        int blockLimit = 3;

        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "paging";
    }
}
