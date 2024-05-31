package com.mazihao.shengxianmarket.common;

import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;

public class ApiResrResponse<T> {
    private Integer status;

    private String msg;

    private T data;

    private static final int OK_CODE = 10000;

    private static final String OK_MSG = "SUCCESS";

    public ApiResrResponse() {
        this(OK_CODE, OK_MSG);
    }

    public ApiResrResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiResrResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResrResponse<T> success() {
        return new ApiResrResponse<>();
    }

    public static <T> ApiResrResponse<T> success(T result) {
        ApiResrResponse<T> response = new ApiResrResponse<>();
        response.setData(result);
        return response;
    }

    public static <T> ApiResrResponse<T> error(Integer code, String msg) {
        return new ApiResrResponse<>(code, msg);
    }

    public static <T> ApiResrResponse<T> error(shengxianmarketExceptionEnum ex) {
        return new ApiResrResponse<>(ex.getCode(), ex.getMsg());
    }

    @Override
    public String toString() {
        return "ApiResrResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
