package com.example.restapi.domain.post.post.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchKeywordType {
    TITLE("TITLE"),
    CONTENT("CONTENT");

    private final String value;
}