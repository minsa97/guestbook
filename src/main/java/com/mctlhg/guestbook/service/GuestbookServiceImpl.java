package com.mctlhg.guestbook.service;

import com.mctlhg.guestbook.dto.GuestbookDTO;
import com.mctlhg.guestbook.dto.PageRequestDTO;
import com.mctlhg.guestbook.dto.PageResultDTO;
import com.mctlhg.guestbook.entity.Guestbook;
import com.mctlhg.guestbook.entity.QGuestbook;
import com.mctlhg.guestbook.repository.GuestbookRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor // 의존성 자동 주입
public class GuestbookServiceImpl implements GuestbookService{
    private final GuestbookRepository repository; // 반드시 final로 선언
    @Override
    public Long register(GuestbookDTO dto){
        log.info("DTO------------------------");
        log.info(dto);

        Guestbook entity=dtoToEntity(dto);
        log.info(entity);

        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO){
        //Pageable pageable=requestDTO.getPageable(Sort.by("gno").descending());
        //Page<Guestbook> result=repository.findAll(pageable);
        //Function<Guestbook, GuestbookDTO> fn=(entity->entityToDto(entity));
        //return new PageResultDTO<>(result, fn);
        Pageable pageable=requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder=getSearch(requestDTO);

        Page<Guestbook> result=repository.findAll(booleanBuilder, pageable); // Querydsl 사용
        Function<Guestbook, GuestbookDTO> fn=(entity-> entityToDto(entity));
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno){
        Optional<Guestbook> result = repository.findById(gno);
        return result.isPresent()? entityToDto(result.get()):null;
    }

    @Override
    public void remove(Long gno){
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto){
        // 여기서 수정하는 항목은 제목과 내용이다.
        Optional<Guestbook> result=repository.findById(dto.getGno());
        if(result.isPresent()){
            Guestbook entity=result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){ //Querydsl을 처리한다.
        String type=requestDTO.getType();

        BooleanBuilder booleanBuilder=new BooleanBuilder();
        QGuestbook qGuestbook=QGuestbook.guestbook;

        String keyword=requestDTO.getKeyword();
        BooleanExpression expression=qGuestbook.gno.gt(0L); // gno>0 조건만 생성한다.
        booleanBuilder.and(expression);

        if(type==null || type.trim().length()==0){ // 검색 조건이 없는 경우
            return booleanBuilder;
        }

        // 검색조건을 작성하기
        BooleanBuilder conditionBuilder=new BooleanBuilder();

        if(type.contains("t")) conditionBuilder.or(qGuestbook.title.contains(keyword));
        if(type.contains("c")) conditionBuilder.or(qGuestbook.content.contains(keyword));
        if(type.contains("w")) conditionBuilder.or(qGuestbook.writer.contains(keyword));

        // 모든 조건을 통합
        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }
}



