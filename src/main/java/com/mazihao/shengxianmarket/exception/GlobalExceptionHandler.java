package com.mazihao.shengxianmarket.exception;

import com.mazihao.shengxianmarket.common.ApiResrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理统一异常的handler
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // 系统异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception:" + e);
        return ApiResrResponse.error(shengxianmarketExceptionEnum.SYSTEM_ERROR);
    }

    // 业务异常
    @ExceptionHandler(shengxianmarketException.class)
    @ResponseBody
    public Object handleshengxianmarketException(shengxianmarketException e){
        log.error("shengxianmarketException:" + e);
        return ApiResrResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResrResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:", e);
        return handleBindingResult(e.getBindingResult());
    }

    private ApiResrResponse handleBindingResult(BindingResult result) {
        // 把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allError = result.getAllErrors();
            for (ObjectError objectError : allError) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiResrResponse.error(shengxianmarketExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
