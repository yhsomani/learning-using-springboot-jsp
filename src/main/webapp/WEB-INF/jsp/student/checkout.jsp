<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Checkout - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        body { background-color: #f8fafc; }
        .checkout-card {
            max-width: 600px;
            margin: 100px auto;
        }
    </style>
</head>
<body>

    <div class="container">
        <div class="glass p-5 card-glass checkout-card">
            <h2 class="fw-bold mb-4"><i class="bi bi-shield-check text-success me-2"></i>Secure Checkout</h2>
            <hr class="mb-4">
            
            <div class="mb-5">
                <h5 class="text-muted small fw-bold mb-2">COURSE SELECTION</h5>
                <div class="d-flex justify-content-between align-items-center">
                    <div class="fw-bold fs-5">${course.title}</div>
                    <div class="fw-bold text-success fs-5">₹499.00</div>
                </div>
                <div class="text-muted small">${course.category} • ${course.difficulty}</div>
            </div>

            <form action="/payments/process" method="POST">
                <input type="hidden" name="courseId" value="${course.id}">
                
                <h5 class="text-muted small fw-bold mb-3">PAYMENT METHOD</h5>
                <div class="form-check p-3 border rounded-3 mb-3 bg-light bg-opacity-50">
                    <input class="form-check-input ms-0 me-3" type="radio" checked>
                    <label class="form-check-input-label fw-bold">
                        <i class="bi bi-credit-card me-2"></i> UPI / Credit / Debit Card
                    </label>
                </div>

                <div class="alert alert-info border-0 rounded-4 small mb-4">
                    <i class="bi bi-info-circle-fill me-2"></i> You will be automatically enrolled and redirected to your dashboard upon success.
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-primary rounded-pill py-3 fw-bold">Complete Purchase</button>
                    <a href="/student/courses" class="btn btn-link text-muted mt-2">Cancel Transaction</a>
                </div>
            </form>
        </div>
    </div>

</body>
</html>
