package com.ddiring.Backend_BlockchainConnector.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    // public으로 컨트롤러에서 호출할 시, 통일성 문제 발생
    // ex. code와 message의 내용이 개발자마다 달라질 수 있음
    private ApiResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private ApiResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> createOK(T data) {
        return new ApiResponseDto<>("OK", "요청이 성공하였습니다.", data);
    }

    public static ApiResponseDto<String> defaultOK() {
        return ApiResponseDto.createOK(null);
    }

    public static ApiResponseDto<String> createError(String code, String message) {
        return new ApiResponseDto<>(code, message);
    }
}
