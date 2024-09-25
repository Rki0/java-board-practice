package com.idontknow.board.dto;

// lombok이 Getter, Setter 자동으로 생성해줌
import com.idontknow.board.entity.BoardEntity;
import com.idontknow.board.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DTO(Data Transfer Object) = 데이터를 전송할 때 사용하는 객체
@Getter
@Setter
@ToString // 보통 필드값 확인할 때 쓰임
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    // 파일이 하나인 경우
//    private MultipartFile boardFile; // save.html -> Controller 파일 담는 용도
//    private String originalFileName; // 원본 파일 이름
//    private String storedFileName; // 서버 저장용 파일 이름

    // 파일이 여러 개인 경우
    private List<MultipartFile> boardFile;
    private List<String> originalFileName; // 원본 파일 이름
    private List<String> storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();

        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        if (boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 0
        } else {
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 1
            // 파일 이름을 가져가야함.
            // originalFileName, storedFileName : board_file_table(BoardFileEntity)
            // 파일 이름에 접근하기 위해서는 BoardFileEntity가 필요한데, 이 메서드에서는 따로 받고 있지 않다.
            // 여기서 JPA의 장점이 하나 등장한다. Entity간 연관 관계 설정을 통해 접근이 가능!
            // Native Query : SELECT * FROM board_table b, board_file_table bf WEHRE b.id=bf.board_id AND WHERE b.id=?

            // 파일이 하나인 경우
//            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName()); // 0번 인덱스에 있는 값을 get 한다는 뜻. 지금은 파일이 하나만 들어가게 되어 있으니까 0으로 하는 것임.
//            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            // 파일이 여러 개인 경우
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();

            for (BoardFileEntity boardFileEntity: boardEntity.getBoardFileEntityList()) {
                originalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }

            boardDTO.setOriginalFileName(originalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);
        }

        return boardDTO;
    }
}
