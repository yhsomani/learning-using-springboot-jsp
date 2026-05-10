<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        body { 
            background: linear-gradient(135deg, #f0fdf4 0%, #d1fae5 100%);
            min-height: 100vh; 
            display: flex; 
            align-items: center; 
            justify-content: center; 
            padding: 40px 20px;
        }
        .register-card { 
            width: 100%; 
            max-width: 550px; 
            padding: 40px; 
        }
        .form-control, .form-select {
            border-radius: 10px;
            padding: 12px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.5);
        }
        .form-control:focus, .form-select:focus {
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
            border-color: var(--primary);
            background: white;
        }
    </style>
</head>
<body>

<div class="glass register-card ">
    <div class="text-center mb-5">
        <a href="/" class="text-decoration-none">
            <h1 class="h3 fw-bold" style="color: var(--primary)">
                <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
            </h1>
        </a>
        <h2 class="h4 mt-4 fw-bold" style="color: var(--text-main)">Create your account</h2>
        <p class="text-muted">Join our community of rural scholars and mentors</p>
    </div>
    
    <c:if test="${not empty errors}">
        <div class="alert alert-danger py-2 mb-4" style="border-radius: 10px; font-size: 0.9rem;">
            <c:forEach items="${errors}" var="error">
                <p class="mb-0"><i class="bi bi-exclamation-circle me-2"></i>${error}</p>
            </c:forEach>
        </div>
    </c:if>

    <form action="/register" method="post">
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-muted">Full Name</label>
                <input type="text" name="fullName" class="form-control" placeholder="John Doe" value="${user.fullName}" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-muted">Email Address</label>
                <input type="email" name="email" class="form-control" placeholder="john@example.com" value="${user.email}" required>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label small fw-bold text-muted">Username</label>
            <input type="text" name="username" class="form-control" placeholder="johndoe123" value="${user.username}" required>
        </div>
        
        <div class="mb-3">
            <label class="form-label small fw-bold text-muted">Password</label>
            <input type="password" name="password" class="form-control" placeholder="Create a password" required>
        </div>
        
        <div class="mb-4">
            <label class="form-label small fw-bold text-muted">I am a...</label>
            <select name="role" class="form-select" required>
                <option value="STUDENT" ${user.role == 'STUDENT' ? 'selected' : ''}>Student</option>
                <option value="TEACHER" ${user.role == 'TEACHER' ? 'selected' : ''}>Teacher</option>
                <option value="PARENT" ${user.role == 'PARENT' ? 'selected' : ''}>Parent</option>
            </select>
        </div>
        
        <button type="submit" class="btn-verdant w-100 py-3 shadow-sm mb-3">Create Account</button>
    </form>
    
    <div class="text-center">
        <p class="text-muted small mb-0">Already have an account? <a href="/login" class="fw-bold text-decoration-none" style="color: var(--secondary)">Log In</a></p>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
