package com.belenits.cashflow.account.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.belenits.cashflow.account.dto.response.ErrorResponse;
import com.belenits.cashflow.account.util.CorrelationIdUtil;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

	private String currentCorrelationId() {
		return MDC.get(CorrelationIdUtil.MDC_KEY);
	}

	@ExceptionHandler(AccountTypeIdNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountTypeNotFound(AccountTypeIdNotFoundException ex) {

		log.warn("Account type not found: {}", ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(false, 404, ex.getMessage(), currentCorrelationId());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {

		log.warn("Account not found: {}", ex.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(false, 404, ex.getMessage(), currentCorrelationId());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

		log.warn("Type mismatch for parameter '{}', value='{}', expectedType={}", ex.getName(), ex.getValue(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

		ErrorResponse errorResponse = new ErrorResponse(false, 400, "Invalid value for parameter '" + ex.getName()
				+ "': expected a valid " + ex.getRequiredType().getSimpleName(), currentCorrelationId());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(AccountAccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccountAccessDenied(AccountAccessDeniedException ex) {

		log.warn("Account access denied: {}", ex.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(false, 403, ex.getMessage(), currentCorrelationId());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

		log.warn("Validation failed: {}", ex.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(false, 400, "Validation failed for request parameters",
				currentCorrelationId());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {

		log.warn("Invalid application state: {}", ex.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(false, 422, ex.getMessage(), currentCorrelationId());

		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
		log.warn("Missing required parameter: {}", ex.getParameterName());
		ErrorResponse errorResponse = new ErrorResponse(false, 400,
				"Required parameter '" + ex.getParameterName() + "' is missing", currentCorrelationId());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(MissingRequestValueException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestValue(MissingRequestValueException ex) {

	    log.warn("Missing required request value: {}", ex.getMessage());

	    ErrorResponse errorResponse = new ErrorResponse(
	            false, 401, "Authentication context missing or invalid", currentCorrelationId());

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

		log.error("Unhandled exception occurred", ex);

		ErrorResponse errorResponse = new ErrorResponse(false, 500, "Internal server error", currentCorrelationId());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

}
