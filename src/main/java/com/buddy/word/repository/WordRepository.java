package com.buddy.word.repository;

import com.buddy.word.model.WordEntry;

import java.util.List;

public interface WordRepository {
    void insert(WordEntry entry);

    List<WordEntry> findByUser(Long userId);
}
