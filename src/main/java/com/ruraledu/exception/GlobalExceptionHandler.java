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

    @ExceptionHandler(CourseNotFoundException.class)
    public Object handleCourseNotFound(CourseNotFoundException ex, HttpServletRequest request) {
        logger.warn("Course not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Object handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "The provided user information already exists in our system.";
        logger.warn("Registration conflict: {}", message);
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("errorMessage", message);
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied for {} on {}: {}", request.getRemoteUser(), request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access Denied: You do not have the required permissions to access this resource."));
        }
        return new ModelAndView("error/403");
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "A system error occurred while processing your request. Our technical team has been notified. Please try again later."));
        }
        // SEC-02 fix: use dedicated 500.jsp instead of reusing 404 page
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", "A system error occurred while processing your request. Our technical team has been notified. Please try again later.");
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (requestUri != null && requestUri.startsWith("/api/")) ||
               (accept != null && accept.contains("application/json"));
    }
}
