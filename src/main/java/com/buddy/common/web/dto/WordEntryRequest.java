package com.buddy.common.web.dto;

public class WordEntryRequest {
    private Long sessionId; // optional
    private Long userId;
    private String phrase;
    private String example;
    private String memo;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPhrase() { return phrase; }
    public void setPhrase(String phrase) { this.phrase = phrase; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
