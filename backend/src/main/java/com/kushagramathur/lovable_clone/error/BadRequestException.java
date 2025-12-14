package com.kushagramathur.lovable_clone.error;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadRequestException extends RuntimeException{
    private final String message;
}
