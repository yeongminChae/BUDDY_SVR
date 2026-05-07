package com.buddy.word.repository;

import com.buddy.word.mapper.WordMapper;
import com.buddy.word.model.WordEntry;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WordRepositoryImpl implements WordRepository {

    private final WordMapper mapper;

    public WordRepositoryImpl(WordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void insert(WordEntry entry) {
        mapper.insert(entry);
    }

    @Override
    public List<WordEntry> findByUser(Long userId) {
        return mapper.findByUser(userId);
    }
}
