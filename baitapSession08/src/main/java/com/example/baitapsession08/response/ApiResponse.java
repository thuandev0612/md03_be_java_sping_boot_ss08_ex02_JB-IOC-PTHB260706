package com.example.baitapsession08.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final String status;
    private final String message;
    private final T data;
}
