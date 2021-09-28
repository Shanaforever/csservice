package com.centricsoftware.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;
import com.centricsoftware.commons.exception.ParamException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理
 * @author ZhengGong
 * @date 2019/5/24
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = NoHandlerFoundException.class)
	public ResEntity handleNoHandlerFoundException(NoHandlerFoundException e) {
		log.info("-------------------------进入全局异常捕获NoHandlerFoundException---------------------------");
		log.error("【全局异常拦截】NoHandlerFoundException: 请求方法 {}, 请求路径 {}", e.getRequestURL(), e.getHttpMethod());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.REQUEST_NOT_FOUND);
	}
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public ResEntity handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.info("-------------------------进入全局异常捕获HttpRequestMethodNotSupportedException---------------------------");
		log.error("【全局异常拦截】HttpRequestMethodNotSupportedException: 当前请求方式 {}, 支持请求方式 {}", e.getMethod(), JSONUtil.toJsonStr(e.getSupportedHttpMethods()));
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.HTTP_BAD_METHOD);
	}
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.info("-------------------------进入全局异常捕获MethodArgumentNotValidException---------------------------");
		log.error("【全局异常拦截】MethodArgumentNotValidException", e);
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.BAD_REQUEST.getCode(), e.getBindingResult()
				.getAllErrors()
				.get(0)
				.getDefaultMessage(), false);
	}
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResEntity handleConstraintViolationException(ConstraintViolationException e) {
		log.info("-------------------------进入全局异常捕获ConstraintViolationException---------------------------");
		log.error("【全局异常拦截】ConstraintViolationException", e);
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.BAD_REQUEST.getCode(), CollUtil.getFirst(e.getConstraintViolations())
				.getMessage(), null);
	}
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResEntity handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.info("-------------------------进入全局异常捕获MethodArgumentTypeMismatchException---------------------------");
		log.error("【全局异常拦截】MethodArgumentTypeMismatchException: 参数名 {}, 异常信息 {}", e.getName(), e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.PARAM_NOT_MATCH);
	}
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public ResEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.info("-------------------------进入全局异常捕获HttpMessageNotReadableException---------------------------");
		log.error("【全局异常拦截】HttpMessageNotReadableException: 错误信息 {}", e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.PARAM_NOT_NULL);
	}
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public ResEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		log.info("-------------------------进入全局异常捕获MissingServletRequestParameterException---------------------------");
		log.error("【全局异常拦截】MissingServletRequestParameterException: 异常信息 {}", e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.PARAM_NOT_MATCH,e.getMessage());
	}
	@ExceptionHandler(value = ParamException.class)
	public ResEntity handleParamException(ParamException e) {
		log.info("-------------------------进入全局异常捕获ParamException---------------------------");
		log.error("【全局异常拦截】ParamException: 异常信息 {}", e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.PARAM_NOT_NULL,e.getMessage());
	}
	@ExceptionHandler(value = JsonProcessingException.class)
	public ResEntity handleJsonProcessingException(JsonProcessingException e) {
		log.info("-------------------------进入全局异常捕获JsonProcessingException---------------------------");
		log.error("【全局异常拦截】JsonProcessingException: 异常信息 {}", e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.JSON_PARSE_ERROR,e.getMessage());
	}
	@ExceptionHandler(value = BaseException.class)
	public ResEntity handleBaseException(BaseException e) {
		log.error("【全局异常拦截】BaseException: 异常信息 {}", e.getMessage());
		log.error("异常栈：",e);
		return WebResponse.failure(ResCode.parse(e.getCode()),e.getMessage());
	}


	@ExceptionHandler(value = Exception.class)
	public ResEntity handleGlobalException(Exception e) {
		log.info("-------------------------进入全局异常捕获Exception---------------------------");
		log.error("【全局异常拦截】: 异常信息 {} ", e.getMessage());
		log.error("异常栈：",e);
		return 	WebResponse.failure(ResCode.SYSTEM_RUNTIME_ERROR,e.getMessage());
	}

	@ExceptionHandler(value = IllegalArgumentException.class)
	public ResEntity handleAssertException(IllegalArgumentException e){
		log.info("-------------------------进入全局异常捕获Exception---------------------------");
		log.error("【全局异常拦截】: 异常信息 {} ", e.getMessage());
		log.error("异常栈：",e);
		return 	WebResponse.failure(ResCode.SYSTEM_RUNTIME_ERROR,e.getMessage());
	}
}
