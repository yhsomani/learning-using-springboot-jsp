<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Access Denied - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center vh-100">
    <div class="text-center glass p-5 card-glass" style="max-width: 500px;">
        <div class="bg-danger-subtle text-danger rounded-circle d-inline-flex p-4 mb-4">
            <i class="bi bi-shield-lock-fill display-4"></i>
        </div>
        <h1 class="fw-bold mb-3">Access Restricted</h1>
        <p class="text-muted mb-4">You do not have the necessary permissions to access this administrative portal. This incident has been logged.</p>
        <div class="d-grid gap-2">
            <a href="/main/dashboard" class="btn btn-primary rounded-pill py-3 fw-bold">Return to Dashboard</a>
            <a href="/logout" class="btn btn-outline-secondary rounded-pill py-3">Logout & Switch Account</a>
        </div>
    </div>
</body>
</html>
