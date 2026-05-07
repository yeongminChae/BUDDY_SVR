package com.buddy.word.controller;

import com.buddy.common.web.ApiResponse;
import com.buddy.common.web.dto.WordEntryRequest;
import com.buddy.word.model.WordEntry;
import com.buddy.word.service.WordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user/words")
public class UserWordController {

    private final WordService wordService;

    public UserWordController(WordService wordService) {
        this.wordService = wordService;
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody WordEntryRequest req) {
        WordEntry e = new WordEntry(
                1L, // TODO, 에러 회피 위해 일시적 값 고정, 추후 고도화 예정
            req.getSessionId(),
            req.getUserId(),
            req.getPhrase(),
            req.getExample(),
            req.getMemo(),
            LocalDateTime.now()
        );

        wordService.add(e);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<WordEntry>> list(@PathVariable Long userId) {
        return ApiResponse.ok(wordService.listByUser(userId));
    }
}
