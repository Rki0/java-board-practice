package com.idontknow.board.service;

import com.idontknow.board.dto.BoardDTO;
import com.idontknow.board.entity.BoardEntity;
import com.idontknow.board.entity.BoardFileEntity;
import com.idontknow.board.repository.BoardFileRepository;
import com.idontknow.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository; // Repository에서 파생되는 메서드들은 매개변수를 Entity 타입으로 받게 되어 있음.
    // 따라서, DTO <-> Entity 변환 작업을 생각하면 진행해야함.
    // DTO -> Entity : Entity class에서 주로 진행됨
    // Entity -> DTO : DTO class에서 주로 진행됨
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        // 파일 첨부 여부에 따라 로직 분리
        if(boardDTO.getBoardFile().isEmpty()){
            // 첨부 파일 없음
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 첨부 파일 있음
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦
                내 사진.jpg => 782349142_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */

//            // 파일이 하나인 경우
//            MultipartFile boardFile = boardDTO.getBoardFile(); // 1.
//
//            String originalFilename = boardFile.getOriginalFilename(); // 2.
//
//            String storedFilename = System.currentTimeMillis() + " " + originalFilename; // 3.
//
////            String savePath = "C:/springboot_img/" + storedFilename; // 4. for Window
//            String savePath = "/Users/pakkiyoung/springboot_img/" + storedFilename; // 4. for Mac
//
//            boardFile.transferTo(new File(savePath)); // 5.
//
//            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
//            Long savedId = boardRepository.save(boardEntity).getId(); // 6.
//
//            BoardEntity board = boardRepository.findById(savedId).get();
//            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(boardEntity, originalFilename, storedFilename);
//            boardFileRepository.save(boardFileEntity); // 7.

            // 파일이 여러 개인 경우
            // 하나의 부모(BoardEntity)에 여러 개의 자식(BoardFileEntity)이 들어가므로, 부모가 먼저 저장되어 있어야 한다.
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId(); // 6.
            BoardEntity board = boardRepository.findById(savedId).get();

            for (MultipartFile boardFile: boardDTO.getBoardFile()) { // 1.
                String originalFilename = boardFile.getOriginalFilename(); // 2.

                String storedFilename = System.currentTimeMillis() + " " + originalFilename; // 3.

    //            String savePath = "C:/springboot_img/" + storedFilename; // 4. for Window
                String savePath = "/Users/pakkiyoung/springboot_img/" + storedFilename; // 4. for Mac

                boardFile.transferTo(new File(savePath)); // 5.

                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(boardEntity, originalFilename, storedFilename);
                boardFileRepository.save(boardFileEntity); // 7.
            }

        }
    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();

        // Entity 객체를 DTO 객체로 옮겨 담아야함
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for(BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

    @Transactional // JPA에서 제공하는 기능을 사용하는 것이 아닌, 커스텀 쿼리 등을 사용할 때 붙여주는 어노테이션
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional // toBoardDTO에서 BoardFileDTO에도 접근하므로 Transactional 어노테이션을 사용해줘야한다.
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);

        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);

        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3;

        // 몇 페이지를 보고 싶은지(page), 한 페이지에 몇 개씩 볼건지(pageLimit)
        // Sort 내부에 작성하는 ...properties는 Entity를 기준으로 한다! DB 컬럼명 기준이 아님!
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        // Entity를 board로 하나씩 꺼내와서 DTO로 옮겨담는 것을 지원하는 Page의 map 메서드. Page 기능을 사용하는 것을 유지하기 위함!
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));

        return boardDTOS;
    }
}
