package com.ruraledu.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Object handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        logger.warn("Registration conflict: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied for {} on {}", request.getRemoteUser(), request.getRequestURI());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access Denied"));
        }
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later."));
        }
        // Use 404 page as fallback since no 500.jsp exists
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (requestUri != null && requestUri.startsWith("/api/")) ||
               (accept != null && accept.contains("application/json"));
    }
}
