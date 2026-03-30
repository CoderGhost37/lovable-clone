package com.kushagramathur.distributed_lovable_clone.common_lib.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException{
    private final String message;
}
