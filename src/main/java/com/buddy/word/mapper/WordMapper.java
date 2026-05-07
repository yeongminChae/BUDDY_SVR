package com.buddy.word.mapper;

import com.buddy.word.model.WordEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WordMapper {
    void insert(WordEntry entry);

    List<WordEntry> findByUser(@Param("userId") Long userId);
}
