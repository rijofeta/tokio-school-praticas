package com.tokioschool.praticas.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private final int statusCode;
    private final String message;
    private final String timestamp;
}
