package org.aionys.main.exceptionhandling;

public record FieldError(String message, String field, String value) {
}