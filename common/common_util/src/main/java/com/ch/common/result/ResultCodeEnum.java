package com.ch.common.result;

import lombok.Getter;

/**
 * @Author hui cao
 * @ClassName: ResultCodeEnum
 * @Description: 全局统一返回结果类
 * @Date: 2023/4/2 18:59
 * @Version: v1.0
 */

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    LOGIN_ERROR(204,"认证失败"),
    DATA_ERROR(204, "数据异常"),

    LOGIN_AUTH(208, "未登陆1111"),
    PERMISSION(209, "没有权限");

    private Integer code;

    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
