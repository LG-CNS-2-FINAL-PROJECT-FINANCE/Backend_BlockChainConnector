package com.ddiring.Backend_BlockchainConnector.advice;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import com.ddiring.Backend_BlockchainConnector.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(value = 1)
@RestControllerAdvice
public class ApiCommonAdvice {
    // 개발자가 정의한 BadParameter 에러가 발생할 시, BAD_REQUEST 에러 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadParameter.class})
    public ApiResponseDto<String> handleBadParameter(BadParameter e) {
        return ApiResponseDto.createError(
          e.getErrorCode(),
          e.getErrorMessage()
        );
    }

    // 개발자가 정의한 NotFound 에러가 발생할 시, NOT_FOUND 에러 처리
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFound.class})
    public ApiResponseDto<String> handleNotFound(NotFound e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    // 개발자가 정의한 ClientError가 발생할 시, BAD_REQUEST 에러 처리
    // ClientError를 상속한 개발자 정의 에러를 throw 하면 실행됨
    // ex. throw new NotFound("에러 메시지");
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ClientError.class})
    public ApiResponseDto<String> handleClientError(ClientError e) {
        return ApiResponseDto.createError(
          e.getErrorCode(),
          e.getErrorMessage()
        );
    }

    // 잘못된 URL에 대해 NOT_FOUND 에러 처리
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoResourceFoundException.class})
    public ApiResponseDto<String> handleNoResourceFoundException(NoResourceFoundException e) {
        return ApiResponseDto.createError(
          "NoResource",
          "잘못된 URL입니다."
        );
    }

    // 모든 에러에 대해 500번 에러 처리 (공통 형식인 ApiResponseDto으로 반환)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ApiResponseDto<String> handlerException(Exception e) {
        return ApiResponseDto.createError(
            "ServerError",
            e.getMessage()
        );
    }
}
