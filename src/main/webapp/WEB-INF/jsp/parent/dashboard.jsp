<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Guardian Portal - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        :root {
            --primary: #4f46e5;
            --primary-light: #818cf8;
            --accent: #f59e0b;
            --glass: rgba(255, 255, 255, 0.9);
            --sidebar-width: 280px;
        }

        body {
            background: #f8fafc;
            font-family: 'Outfit', sans-serif;
            overflow-x: hidden;
        }

        .dashboard-container {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar Styling */
        .sidebar {
            width: var(--sidebar-width);
            background: #1e293b;
            color: white;
            padding: 2rem 1.5rem;
            position: fixed;
            height: 100vh;
            transition: all 0.3s ease;
            z-index: 1000;
        }

        .sidebar-brand {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 3rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            color: white;
            text-decoration: none;
        }

        .nav-link {
            color: #94a3b8;
            padding: 0.8rem 1rem;
            border-radius: 12px;
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            transition: all 0.2s;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .nav-link.active {
            background: var(--primary);
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
        }

        /* Main Content Styling */
        .main-content {
            flex: 1;
            margin-left: var(--sidebar-width);
            padding: 2rem 3rem;
        }

        .header-section {
            margin-bottom: 3rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .welcome-card {
            background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
            color: white;
            padding: 2.5rem;
            border-radius: 24px;
            margin-bottom: 3rem;
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .welcome-card::after {
            content: '';
            font-family: "bootstrap-icons";
            position: absolute;
            right: -20px;
            bottom: -20px;
            font-size: 10rem;
            opacity: 0.05;
        }

        /* Child Cards */
        .child-card {
            background: white;
            border-radius: 20px;
            border: 1px solid rgba(0,0,0,0.05);
            padding: 1.5rem;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            height: 100%;
        }

        .child-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.05);
        }

        .avatar-circle {
            width: 60px;
            height: 60px;
            background: #eef2ff;
            color: var(--primary);
            border-radius: 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }

        .stat-badge {
            padding: 0.4rem 0.8rem;
            border-radius: 10px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .badge-courses { background: #ecfeff; color: #0891b2; }
        .badge-progress { background: #f0fdf4; color: #16a34a; }

        /* Progress Mini */
        .progress-compact {
            height: 8px;
            border-radius: 4px;
            background: #f1f5f9;
        }

        .add-child-btn {
            background: white;
            border: 2px dashed #cbd5e1;
            border-radius: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 1rem;
            color: #64748b;
            height: 100%;
            min-height: 250px;
            transition: all 0.2s;
        }

        .add-child-btn:hover {
            border-color: var(--primary);
            color: var(--primary);
            background: #f5f3ff;
        }

        .modal-content {
            border-radius: 24px;
            border: none;
            padding: 1rem;
        }

        .form-control {
            border-radius: 12px;
            padding: 0.8rem 1rem;
            border: 1px solid #e2e8f0;
        }

        .btn-primary {
            background: var(--primary);
            border: none;
            border-radius: 12px;
            padding: 0.8rem 2rem;
            font-weight: 600;
        }

        @media (max-width: 992px) {
            .sidebar { transform: translateX(-100%); }
            .main-content { margin-left: 0; padding: 1.5rem; }
        }
    </style>
</head>
<body>

<div class="dashboard-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <a href="/" class="sidebar-brand">
            <i class="bi bi-shield-check"></i>
            <span>RuralEduHub</span>
        </a>
        
        <nav class="nav flex-column mt-4">
            <a class="nav-link active" href="/parent/dashboard"><i class="bi bi-grid"></i> Dashboard</a>
            <a class="nav-link" href="/parent/dashboard"><i class="bi bi-people"></i> My Children</a>
            <a class="nav-link" href="/parent/dashboard"><i class="bi bi-chat-dots"></i> Messages</a>
            <a class="nav-link" href="/parent/dashboard"><i class="bi bi-journal-text"></i> Reports</a>
            <hr class="my-4 opacity-10">
            <a class="nav-link" href="/parent/dashboard"><i class="bi bi-person-gear"></i> Settings</a>
            <a class="nav-link text-danger" href="/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
        </nav>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <div class="header-section">
            <div>
                <h6 class="text-muted text-uppercase fw-bold mb-1" style="letter-spacing: 1px; font-size: 0.75rem;">Guardian Portal</h6>
                <h2 class="fw-bold mb-0">Hello, ${parent.fullName}</h2>
            </div>
            <div class="d-flex gap-3">
                <button class="btn btn-white shadow-sm rounded-pill px-4">
                    <i class="bi bi-bell me-2"></i> Notifications
                </button>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success border-0 rounded-4 shadow-sm mb-4">
                <i class="bi bi-check-circle-fill me-2"></i> ${success}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger border-0 rounded-4 shadow-sm mb-4">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> ${error}
            </div>
        </c:if>

        <div class="welcome-card">
            <div class="row align-items-center">
                <div class="col-lg-7">
                    <h3 class="fw-bold mb-3">Empowering the next generation</h3>
                    <p class="opacity-75 mb-4">Track your children's educational journey and support their growth in real-time. You currently have ${childStats.size()} student(s) linked to your account.</p>
                    <button class="btn btn-light rounded-pill px-4 fw-bold text-primary" data-bs-toggle="modal" data-bs-target="#addChildModal">
                        <i class="bi bi-plus-lg me-2"></i> Add New Student
                    </button>
                </div>
            </div>
        </div>

        <h4 class="fw-bold mb-4">Student Progress Overview</h4>
        <div class="row g-4">
            <c:forEach items="${childStats}" var="stat">
                <div class="col-md-6 col-lg-4">
                    <div class="child-card">
                        <div class="d-flex justify-content-between align-items-start mb-4">
                            <div class="avatar-circle">
                                ${stat.user.fullName.substring(0,1)}
                            </div>
                            <div class="dropdown">
                                <button class="btn btn-link text-muted p-0" data-bs-toggle="dropdown">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end border-0 shadow-sm rounded-3">
                                    <li><a class="dropdown-item" href="/parent/dashboard">View Details</a></li>
                                    <li>
                                        <form action="/parent/remove-child" method="POST" onsubmit="return confirm('Are you sure you want to unlink this student?')">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <input type="hidden" name="childId" value="${stat.user.id}">
                                            <button type="submit" class="dropdown-item text-danger">Unlink Student</button>
                                        </form>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        
                        <h5 class="fw-bold mb-1">${stat.user.fullName}</h5>
                        <p class="text-muted small mb-4">@${stat.user.username}</p>
                        
                        <div class="d-flex gap-2 mb-4">
                            <span class="stat-badge badge-courses">
                                <i class="bi bi-book me-1"></i> ${stat.courseCount} Courses
                            </span>
                            <span class="stat-badge badge-progress">
                                <i class="bi bi-star me-1"></i> ${stat.user.points} Points
                            </span>
                        </div>

                        <div class="mb-2 d-flex justify-content-between">
                            <span class="small fw-semibold text-muted">Average Progress</span>
                            <span class="small fw-bold text-primary">
                                <fmt:formatNumber value="${stat.avgProgress}" maxFractionDigits="0"/>%
                            </span>
                        </div>
                        <div class="progress progress-compact">
                            <div class="progress-bar bg-primary" style="width: ${stat.avgProgress}%"></div>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <div class="col-md-6 col-lg-4">
                <button class="add-child-btn w-100" data-bs-toggle="modal" data-bs-target="#addChildModal">
                    <div class="avatar-circle" style="background: #f1f5f9; color: #94a3b8;">
                        <i class="bi bi-plus-lg"></i>
                    </div>
                    <span class="fw-semibold">Add Another Student</span>
                </button>
            </div>
        </div>
    </main>
</div>

<!-- Add Child Modal -->
<div class="modal fade" id="addChildModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header border-0">
                <h5 class="fw-bold mb-0">Link Student Account</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body py-4">
                <p class="text-muted mb-4">Enter your child's username to link their account and track their progress.</p>
                <form action="/parent/add-child" method="POST">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-4">
                        <label class="form-label fw-semibold">Student Username</label>
                        <input type="text" name="childUsername" class="form-control" placeholder="e.g. aryan_123" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 shadow-sm">
                        <i class="bi bi-link-45deg me-2"></i> Link Account
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
