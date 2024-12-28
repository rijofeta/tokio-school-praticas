package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.exceptions.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Controller
public class AppErrorController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(AppErrorController.class);

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ErrorResponse errorResponse;
        String logMessage;
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    messageSourceAccessor.getMessage("error.page_not_found"),
                    ZonedDateTime.now(ZoneOffset.UTC).toString()
            );
            logMessage = "Attempt to access non-existent endpoint.";
        } else {
            errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    messageSourceAccessor.getMessage("error.unexpected"),
                    ZonedDateTime.now(ZoneOffset.UTC).toString()
            );
            logMessage = "An unexpected error occured.";
        }
        logger.atInfo()
                .setMessage(logMessage)
                .addKeyValue("status", statusCode)
                .log();
        model.addAttribute("errorResponse", errorResponse);
        return "/error";
    }
}
