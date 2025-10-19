package org.example.backend.handler;

import org.example.backend.dto.response.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //捕获所有的RunTimeException
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleException(RuntimeException e) {
        return Result.error(e.getMessage());
    }


    //通常异常兜底
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("服务器内部错误，请稍后再试");
    }
}
