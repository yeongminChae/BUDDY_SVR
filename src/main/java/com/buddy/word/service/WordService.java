package com.buddy.word.service;

import com.buddy.word.model.WordEntry;

import java.util.List;

public interface WordService {
    void add(WordEntry entry);

    List<WordEntry> listByUser(Long userId);
}
