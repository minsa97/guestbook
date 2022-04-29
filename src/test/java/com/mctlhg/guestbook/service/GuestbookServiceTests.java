package com.mctlhg.guestbook.service;

import com.mctlhg.guestbook.dto.GuestbookDTO;
import com.mctlhg.guestbook.dto.PageRequestDTO;
import com.mctlhg.guestbook.dto.PageResultDTO;
import com.mctlhg.guestbook.entity.Guestbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GuestbookServiceTests {
    @Autowired
    private GuestbookService service;

    @Test
    public void testRegister(){
        GuestbookDTO guestbookDTO=GuestbookDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user0")
                .build();

        System.out.println(service.register(guestbookDTO));
    }

    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<GuestbookDTO, Guestbook> resultDTO=service.getList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());
        System.out.println("start: "+resultDTO.getStart());
        System.out.println("tempEnd: "+resultDTO.getEnd());
        System.out.println("page: "+resultDTO.getPage());

        System.out.println("---------------------------------------------");
        for(GuestbookDTO guestbookDTO:resultDTO.getDtoList()){
            System.out.println(guestbookDTO);
        }

        System.out.println("==============================================");
        resultDTO.getPageList().forEach(i-> System.out.println(i));
    }

    @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("tc") // 검색조건 t,c,w,tc,tcw ...
                .keyword("한글") // 검색키워드
                .build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO=service.getList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());
        System.out.println("------------------------------------------");

        for(GuestbookDTO guestbookDTO: resultDTO.getDtoList()){
            System.out.println(guestbookDTO);
        }

        System.out.println("------------------------------------------");
        resultDTO.getPageList().forEach(i-> System.out.println(i));
    }
}

