package com.buddy.word.service;

import com.buddy.word.model.WordEntry;
import com.buddy.word.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;

    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public void add(WordEntry entry) {

        wordRepository.insert(entry);
    }

    @Override
    public List<WordEntry> listByUser(Long userId) {
        return wordRepository.findByUser(userId);
    }
}
