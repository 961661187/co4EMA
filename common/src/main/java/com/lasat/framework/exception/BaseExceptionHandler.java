package com.lasat.framework.exception;

import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BaseExceptionHandler {

    /**
     * excepiton handler
     * @param e the exception
     * @return result including exception message
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> error(Exception e) {
        e.printStackTrace();
        return new Result<>(false, StatusCode.ERROR, e.getMessage());
    }
}