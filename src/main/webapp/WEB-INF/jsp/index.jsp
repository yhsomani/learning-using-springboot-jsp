<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RuralEduHub - Empowering Rural Education</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-custom">
    <div class="container">
        <a class="navbar-brand" href="/">
            <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/login">Login</a></li>
                <li class="nav-item"><a class="nav-link btn-gold ms-lg-3" href="/register" style="padding: 8px 20px;">Get Started</a></li>
            </ul>
        </div>
    </div>
</nav>

<header class="hero-section">
    <div class="container position-relative" style="z-index: 1;">
        <div class="row align-items-center">
            <div class="col-lg-6 ">
                <h1 class="hero-title">Bridging the Gap in <br><span style="color: var(--secondary)">Rural Education</span></h1>
                <p class="lead mb-5" style="color: var(--text-muted); font-size: 1.25rem;">
                    Adaptive E-Learning with Gamification, Offline Sync, and Real-Time Mentoring. Empowering scholars across boundaries.
                </p>
                <div class="d-flex gap-3">
                    <a href="/register" class="btn-verdant shadow-sm">Join the Academy</a>
                    <a href="#features" class="btn btn-outline-success border-2" style="border-radius: 12px; font-weight: 600; padding: 10px 25px;">Explore Features</a>
                </div>
            </div>
            <div class="col-lg-6 d-none d-lg-block ">
                <div class="glass p-4 card-glass" style="transform: rotate(2deg);">
                    <div class="d-flex align-items-center mb-4">
                        <div class="bg-primary-light p-3 rounded-circle me-3">
                            <i class="bi bi-lightning-charge-fill fs-2 text-success"></i>
                        </div>
                        <div>
                            <h4 class="mb-0">Adaptive Learning</h4>
                            <p class="text-muted mb-0">Powered by AI</p>
                        </div>
                    </div>
                    <div class="progress mb-3" style="height: 10px;">
                        <div class="progress-bar bg-success" role="progressbar" style="width: ${avgProgress}%"></div>
                    </div>
                    <p class="small text-muted">Join ${totalStudents} scholars across ${totalCourses} courses.</p>
                </div>
            </div>
        </div>
    </div>
</header>

<section id="features" class="py-5">
    <div class="container py-5">
        <div class="text-center mb-5">
            <h2 class="fw-bold" style="color: var(--text-main)">Why Choose RuralEduHub?</h2>
            <div class="mx-auto" style="width: 80px; height: 4px; background: var(--secondary); border-radius: 2px;"></div>
        </div>
        <div class="row g-4">
            <div class="col-md-4">
                <div class="glass card-glass h-100 text-center">
                    <div class="mb-4 text-success display-4"><i class="bi bi-controller"></i></div>
                    <h3 class="h4 fw-bold">Gamification</h3>
                    <p class="text-muted">Earn points, badges, and compete on leaderboards while you learn. Making education fun and engaging.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="glass card-glass h-100 text-center">
                    <div class="mb-4 text-warning display-4"><i class="bi bi-cloud-download"></i></div>
                    <h3 class="h4 fw-bold">Offline-First</h3>
                    <p class="text-muted">Download content and learn without an active internet connection. Designed for rural connectivity challenges.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="glass card-glass h-100 text-center">
                    <div class="mb-4 text-info display-4"><i class="bi bi-chat-dots"></i></div>
                    <h3 class="h4 fw-bold">Real-Time Chat</h3>
                    <p class="text-muted">Connect with mentors instantly for guidance and support. You're never alone in your learning journey.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<footer class="py-5" style="background: var(--text-main); color: rgba(255,255,255,0.7);">
    <div class="container text-center">
        <div class="navbar-brand text-white mb-4 d-inline-block">
            <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
        </div>
        <p class="mb-4">Contributing to UN Sustainable Development Goals (SDG 4, 8, & 10)</p>
        <div class="mb-4">
            <a href="#" class="text-white mx-3"><i class="bi bi-facebook"></i></a>
            <a href="#" class="text-white mx-3"><i class="bi bi-twitter-x"></i></a>
            <a href="#" class="text-white mx-3"><i class="bi bi-linkedin"></i></a>
        </div>
        <hr style="background: rgba(255,255,255,0.1);">
        <p class="small mb-0">&copy; 2026 RuralEduHub. All rights reserved.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
