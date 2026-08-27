package com.belenits.cashflow.account.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.belenits.cashflow.account.dto.response.ErrorResponse;
import com.belenits.cashflow.account.util.CorrelationIdUtil;

import jakarta.validation.ConstraintViolationException;

class GlobalExceptionHandleTest {

    private GlobalExceptionHandle handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandle();
        MDC.put(CorrelationIdUtil.MDC_KEY, "corr-123");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("handleAccountTypeNotFound returns 404 with message and correlationId")
    void handleAccountTypeNotFound_returnsNotFound() {
        AccountTypeIdNotFoundException ex = new AccountTypeIdNotFoundException("Account type not found");

        ResponseEntity<ErrorResponse> response = handler.handleAccountTypeNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Account type not found", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleAccountNotFound returns 404")
    void handleAccountNotFound_returnsNotFound() {
        AccountNotFoundException ex = new AccountNotFoundException("Account with id 10 not found");

        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Account with id 10 not found", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleTypeMismatch returns 400 with parameter-specific message")
    void handleTypeMismatch_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("userId");
        when(ex.getValue()).thenReturn("abc");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals(
                "Invalid value for parameter 'userId': expected a valid Long",
                response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleAccountAccessDenied returns 403")
    void handleAccountAccessDenied_returnsForbidden() {
        AccountAccessDeniedException ex = new AccountAccessDeniedException("Forbidden");

        ResponseEntity<ErrorResponse> response = handler.handleAccountAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(403, response.getBody().getStatusCode());
        assertEquals("Forbidden", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleConstraintViolation returns 400 with generic validation message")
    void handleConstraintViolation_returnsBadRequest() {
        ConstraintViolationException ex = new ConstraintViolationException("validation failed", Set.of());

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Validation failed for request parameters", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleIllegalState returns 422")
    void handleIllegalState_returnsUnprocessableEntity() {
        IllegalStateException ex = new IllegalStateException("Unknown account type code");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(422, response.getBody().getStatusCode());
        assertEquals("Unknown account type code", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleMissingParameter returns 400 with parameter name")
    void handleMissingParameter_returnsBadRequest() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("userId", "Long");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Required parameter 'userId' is missing", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleMissingRequestValue returns 401")
    void handleMissingRequestValue_returnsUnauthorized() {
        MissingRequestValueException ex = mock(MissingRequestValueException.class);
        when(ex.getMessage()).thenReturn("Missing auth context");

        ResponseEntity<ErrorResponse> response = handler.handleMissingRequestValue(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(401, response.getBody().getStatusCode());
        assertEquals("Authentication context missing or invalid", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }

    @Test
    @DisplayName("handleGenericException returns 500")
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(500, response.getBody().getStatusCode());
        assertEquals("Internal server error", response.getBody().getErrorMessage());
        assertEquals("corr-123", response.getBody().getCorrelationId());
    }
}
