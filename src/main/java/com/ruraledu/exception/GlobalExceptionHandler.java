package com.ruraledu.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CourseNotFoundException.class)
    public Object handleCourseNotFound(CourseNotFoundException ex, HttpServletRequest request) {
        logger.warn("Course not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "The requested course could not be found."));
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage() != null ? ex.getMessage() : "The requested course could not be found.");
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        String msg = ex.getMessage() != null ? ex.getMessage() : "Invalid input provided. Please review your information and try again.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", msg));
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", msg);
        return mav;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Object handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "The provided username or email is already associated with an existing account.";
        logger.warn("Registration conflict: {}", message);
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        ModelAndView mav = new ModelAndView("register");
        // Update to match how the JSP renders errors
        mav.addObject("errors", Collections.singletonList(message));
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied for {} on {}: {}", request.getRemoteUser(), request.getRequestURI(), ex.getMessage());
        String msg = "You do not have the required permissions to view this page or perform this action.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", msg));
        }
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", msg);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String msg = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred while processing your request. Please try again later or contact support if the issue persists.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", msg));
        }
        // Use 404 page as fallback since no 500.jsp exists
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", msg);
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (requestUri != null && requestUri.startsWith("/api/")) ||
               (accept != null && accept.contains("application/json"));
    }
}
