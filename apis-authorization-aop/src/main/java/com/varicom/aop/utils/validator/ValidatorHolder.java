package com.varicom.aop.utils.validator;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.varicom.aop.utils.AssertUtils;
import com.google.common.collect.Lists;

/**
 * JSR303 Validator(Hibernate Validator)工具类, 持有Validator单例提供静态的validate方法.
 * 
 */
public class ValidatorHolder implements DisposableBean {

	private static Validator validator;

	@Autowired
	public void setValidator(Validator validator) {
		ValidatorHolder.validator = validator; //NOSONAR
	}

	public static Validator getValidator() {
		assertValidatorInjected();
		return validator;
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时返回ConstraintViolation组成的Set.
	 */
	public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		assertValidatorInjected();
		return getValidator().validate(object, groups);
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException.
	 */
	public static void validateWithException(Object object, Class<?>... groups) throws ConstraintViolationException {
		Set constraintViolations = validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为字符串, 以separator分割.
	 */
	public static String convertMessage(Set<? extends ConstraintViolation> constraintViolations, String separator) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getMessage());
		}
		return StringUtils.join(errorMessages, separator);
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为字符串, 以separator分割.
	 */
	public static String convertMessage(ConstraintViolationException e, String separator) {
		return convertMessage(e.getConstraintViolations(), separator);
	}

	/**
	 * 清除Holder中的validator为null.
	 */
	public static void clearHolder() {
		validator = null;
	}

	@Override
	public void destroy() throws Exception {
		ValidatorHolder.clearHolder();

	}

	private static void assertValidatorInjected() {
		AssertUtils.state(validator != null, "Validator属性未注入.");
	}
}
