<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Explore Courses - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        .course-page-header {
            padding: 60px 0;
            background: linear-gradient(to bottom, #f0fdf4, #ffffff);
        }
        .course-card {
            transition: var(--transition);
            border-radius: 20px;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        .course-card:hover {
            transform: translateY(-10px);
        }
        .search-container {
            max-width: 600px;
            margin: -35px auto 0;
        }
        .search-input {
            padding: 15px 25px;
            border-radius: 50px;
            border: 0;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05);
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-custom sticky-top">
    <div class="container">
        <a class="navbar-brand" href="/student/dashboard">
            <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item"><a class="nav-link" href="/student/dashboard">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active" href="/student/courses">Explore</a></li>
                <li class="nav-item ms-lg-3">
                    <a href="/logout" class="btn btn-outline-light btn-sm rounded-pill px-3">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<header class="course-page-header text-center">
    <div class="container animate-fade">
        <h1 class="display-5 fw-bold mb-3" style="color: var(--text-main)">Discover Your Potential</h1>
        <p class="text-muted lead mx-auto" style="max-width: 700px;">Access high-quality education designed for rural empowerment. Learn from the best mentors, anywhere, anytime.</p>
    </div>
</header>

<div class="container mb-5">
    <form action="/student/courses" method="GET" class="search-container animate-fade" style="animation-delay: 0.1s;">
        <div class="input-group">
            <input type="text" name="search" class="form-control search-input" placeholder="Search for courses, skills, or mentors..." value="${searchKeyword}">
            <button class="btn btn-verdant px-4 rounded-pill ms-2 shadow-sm" type="submit">
                <i class="bi bi-search"></i>
            </button>
        </div>
    </form>

    <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4 mt-5 animate-fade" style="animation-delay: 0.2s;">
        <c:forEach var="course" items="${courses}">
            <div class="col">
                <div class="glass course-card card-glass">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="badge bg-primary-light text-success rounded-pill px-3 py-2 small fw-bold">
                            ${course.category}
                        </span>
                        <div class="small text-muted">
                            <i class="bi bi-bar-chart-steps me-1"></i> ${course.difficulty}
                        </div>
                    </div>
                    <h4 class="h5 fw-bold mb-3" style="color: var(--text-main)">${course.title}</h4>
                    <p class="small text-muted mb-4 flex-grow-1">
                        ${course.description.length() > 120 ? course.description.substring(0, 120).concat('...') : course.description}
                    </p>
                    <div class="d-flex justify-content-between align-items-center pt-3 border-top border-opacity-10">
                        <div class="d-flex align-items-center">
                            <i class="bi bi-person-circle me-2 text-primary"></i>
                            <span class="small text-muted">${course.teacher != null ? course.teacher.fullName : 'Expert Mentor'}</span>
                        </div>
                        <a href="/payments/checkout/${course.id}" class="btn-verdant px-3 py-2 small" style="font-size: 0.8rem;">Enroll Now</a>
                    </div>
                </div>
            </div>
        </c:forEach>
        
        <c:if test="${empty courses}">
            <div class="col-12 text-center py-5">
                <div class="glass p-5">
                    <i class="bi bi-search display-1 text-muted opacity-25"></i>
                    <h4 class="mt-3 text-muted fw-bold">No courses found</h4>
                    <p>We're adding new content every week. Please check back soon!</p>
                </div>
            </div>
        </c:if>
    </div>
</div>

<footer class="py-5 mt-5" style="background: var(--text-main); color: rgba(255,255,255,0.7);">
    <div class="container text-center">
        <p class="small mb-0">&copy; 2026 RuralEduHub. Contributing to SDG 4: Quality Education.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
