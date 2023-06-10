package com.ch.common.config.exception;

import com.ch.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @Author hui cao
 * @ClassName: ChException
 * @Description: 自定义异常类
 * @Date: 2023/4/3 20:11
 * @Version: v1.0
 */

@Data
public class ChException extends RuntimeException{
    private Integer code; //状态码
    private String msg;   //描述信息

    public ChException(Integer code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public ChException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "ChException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
