package com.eazy.pay.mapper;

import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QnaMapper {

    QnaMapper INSTANCE = Mappers.getMapper(QnaMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "userId", source = "user.uid")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "answered", source = "answered")
    @Mapping(target = "answer", source = "answer")
    QnaDTO toDTO(Question question);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "user.uid", source = "userId")
    @Mapping(target = "user.name", source = "userName")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "answered", source = "answered")
    @Mapping(target = "answer", source = "answer")
    Question toEntity(QnaDTO qnaDTO);
}
