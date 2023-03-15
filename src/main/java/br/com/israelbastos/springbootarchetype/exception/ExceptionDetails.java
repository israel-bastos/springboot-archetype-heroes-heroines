package br.com.israelbastos.springbootarchetype.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
@Data
@SuperBuilder
public class ExceptionDetails {
    protected LocalDateTime timestamp;

    protected int statusCode;

    protected String error;

    protected String message;
}
