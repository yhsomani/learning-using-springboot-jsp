<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Server Error - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center vh-100">
    <div class="text-center glass p-5 card-glass" style="max-width: 520px;">
        <div class="bg-danger-subtle text-danger rounded-circle d-inline-flex p-4 mb-4">
            <i class="bi bi-exclamation-triangle-fill display-4"></i>
        </div>
        <h1 class="fw-bold mb-3">500 - Something Went Wrong</h1>
        <p class="text-muted mb-2">An unexpected error occurred while processing your request.</p>
        <c:if test="${not empty message}">
            <p class="text-muted small fst-italic mb-4">${message}</p>
        </c:if>
        <p class="text-muted mb-4">Our technical team has been notified. Please try again later.</p>
        <div class="d-grid gap-2">
            <a href="/" class="btn btn-primary rounded-pill py-3 fw-bold">Go to Homepage</a>
            <button onclick="window.history.back()" class="btn btn-outline-secondary rounded-pill py-3">Go Back</button>
        </div>
    </div>
</body>
</html>
