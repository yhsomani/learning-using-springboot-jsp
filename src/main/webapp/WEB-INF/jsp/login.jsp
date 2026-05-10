<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        body { 
            background: linear-gradient(135deg, #f0fdf4 0%, #d1fae5 100%);
            height: 100vh; 
            display: flex; 
            align-items: center; 
            justify-content: center; 
        }
        .login-card { 
            width: 100%; 
            max-width: 450px; 
            padding: 40px; 
        }
        .form-control {
            border-radius: 10px;
            padding: 12px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.5);
        }
        .form-control:focus {
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
            border-color: var(--primary);
            background: white;
        }
    </style>
</head>
<body>

<div class="glass login-card ">
    <div class="text-center mb-5">
        <a href="/" class="text-decoration-none">
            <h1 class="h3 fw-bold" style="color: var(--primary)">
                <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
            </h1>
        </a>
        <h2 class="h4 mt-4 fw-bold" style="color: var(--text-main)">Welcome Back</h2>
        <p class="text-muted">Enter your credentials to access your portal</p>
    </div>

    <c:if test="${param.error != null}">
        <div class="alert alert-danger py-2" style="border-radius: 10px; font-size: 0.9rem;">
            Invalid username or password.
        </div>
    </c:if>

    <form action="/login" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="mb-3">
            <label class="form-label small fw-bold text-muted">Username</label>
            <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0 text-muted" style="border-radius: 10px 0 0 10px;">
                    <i class="bi bi-person"></i>
                </span>
                <input type="text" name="username" class="form-control border-start-0" placeholder="Your username" style="border-radius: 0 10px 10px 0;" required>
            </div>
        </div>
        <div class="mb-4">
            <label class="form-label small fw-bold text-muted">Password</label>
            <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0 text-muted" style="border-radius: 10px 0 0 10px;">
                    <i class="bi bi-lock"></i>
                </span>
                <input type="password" name="password" class="form-control border-start-0" placeholder="Password" style="border-radius: 0 10px 10px 0;" required>
            </div>
        </div>
        <button type="submit" class="btn-verdant w-100 py-3 shadow-sm mb-3">Sign In</button>
    </form>
    
    <div class="text-center">
        <p class="text-muted small mb-0">Don't have an account? <a href="/register" class="fw-bold text-decoration-none" style="color: var(--secondary)">Create one now</a></p>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
