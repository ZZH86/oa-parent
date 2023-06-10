package com.ch.common.config.exception;

import com.ch.common.result.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author hui cao
 * @ClassName: GlobalExceptionHandler
 * @Description: 全局异常处理器
 * @Date: 2023/4/3 19:54
 * @Version: v1.0
 */
//controller 增强器 It is typically used to define @ExceptionHandler, @InitBinder, and @ModelAttribute methods that apply to all @RequestMapping methods.
@RestControllerAdvice
public class GlobalExceptionHandler {

    //全局异常处理，执行的方法
    @ExceptionHandler(Exception.class)
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("执行全局异常处理");
    }

    //特定异常处理（优先特定异常处理，然后再进行全局处理）
    @ExceptionHandler(ArithmeticException.class)
    public Result error_arithmetic(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("执行特定异常处理");
    }

    //自定义异常处理
    @ExceptionHandler(ChException.class)
    public Result error(ChException e){
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }

    /**
     * spring security异常
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.fail().code(205).message("没有权限！");
    }




}
