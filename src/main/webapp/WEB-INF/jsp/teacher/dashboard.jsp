<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Dashboard - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <style>
        .teacher-hero {
            padding: 60px 0 100px 0;
            background: linear-gradient(135deg, #064e3b 0%, #10b981 100%);
            color: white;
            border-radius: 0 0 50px 50px;
        }
        .stat-card {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            padding: 25px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 10px 30px rgba(0,0,0,0.05);
        }
        .course-item {
            background: white;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 20px;
            transition: var(--transition);
            border: 1px solid #f0fdf4;
        }
        .course-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.05);
            border-color: var(--primary);
        }
        .sidebar-item {
            padding: 12px 20px;
            border-radius: 12px;
            cursor: pointer;
            transition: var(--transition);
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;
        }
        .sidebar-item:hover {
            background: var(--primary-light);
            color: var(--primary);
        }
        .sidebar-item.active {
            background: var(--primary);
            color: white;
        }
    </style>
</head>
<body style="background-color: #f9fafb;">

<nav class="navbar navbar-expand-lg navbar-custom sticky-top">
    <div class="container">
        <a class="navbar-brand" href="/teacher/dashboard">
            <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub <span class="badge bg-gold text-dark ms-2 small" style="font-size: 0.6rem;">TEACHER</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item">
                    <a class="nav-link" href="/teacher/dashboard"><i class="bi bi-bell-fill"></i></a>
                </li>
                <li class="nav-item ms-3">
                    <div class="d-flex align-items-center bg-white bg-opacity-10 rounded-pill px-3 py-1">
                        <span class="small me-2">Hello, ${user.fullName}</span>
                        <div class="bg-gold rounded-circle d-flex align-items-center justify-content-center text-dark fw-bold" style="width: 30px; height: 30px; font-size: 0.8rem;">
                            ${user.fullName.substring(0,1)}
                        </div>
                    </div>
                </li>
                <li class="nav-item ms-3">
                    <a href="/logout" class="btn btn-outline-light btn-sm rounded-pill px-3">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<header class="teacher-hero">
    <div class="container animate-fade">
        <div class="row align-items-center">
            <div class="col-lg-8">
                <h1 class="display-5 fw-bold mb-2">Mentor Dashboard</h1>
                <p class="lead opacity-75">Empower the next generation of rural talent with your expertise.</p>
            </div>
            <div class="col-lg-4 text-lg-end mt-4 mt-lg-0">
                <button class="btn btn-gold btn-lg shadow-sm rounded-pill px-4" data-bs-toggle="modal" data-bs-target="#importModal">
                    <i class="bi bi-plus-circle me-2"></i>Create New Course
                </button>
            </div>
        </div>
    </div>
</header>

<div class="container mt-n5">
    <div class="row g-4">
        <div class="col-md-4 animate-fade" style="animation-delay: 0.1s;">
            <div class="stat-card d-flex align-items-center">
                <div class="bg-primary-light p-4 rounded-4 me-4 text-success">
                    <i class="bi bi-book fs-2"></i>
                </div>
                <div>
                    <h3 class="fw-bold mb-0">${courses.size()}</h3>
                    <p class="text-muted small mb-0">Active Courses</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 animate-fade" style="animation-delay: 0.2s;">
            <div class="stat-card d-flex align-items-center">
                <div class="bg-info bg-opacity-10 p-4 rounded-4 me-4 text-info">
                    <i class="bi bi-people fs-2"></i>
                </div>
                <div>
                    <h3 class="fw-bold mb-0">${totalStudents}</h3>
                    <p class="text-muted small mb-0">Total Students</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 animate-fade" style="animation-delay: 0.3s;">
            <div class="stat-card d-flex align-items-center">
                <div class="bg-warning bg-opacity-10 p-4 rounded-4 me-4 text-warning">
                    <i class="bi bi-graph-up-arrow fs-2"></i>
                </div>
                <div>
                    <h3 class="fw-bold mb-0">${averageProgress}%</h3>
                    <p class="text-muted small mb-0">Avg Student Progress</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row mt-5">
        <div class="col-lg-3">
            <div class="glass p-4 card-glass mb-4">
                <h6 class="fw-bold text-muted small text-uppercase mb-3">Management</h6>
                <div class="sidebar-item active">
                    <i class="bi bi-grid-fill"></i> Dashboard
                </div>
                <div class="sidebar-item">
                    <i class="bi bi-journal-text"></i> My Courses
                </div>
                <div class="sidebar-item">
                    <i class="bi bi-person-badge"></i> Students
                </div>
                <div class="sidebar-item">
                    <i class="bi bi-chat-dots"></i> Q&A Support
                </div>
                
                <h6 class="fw-bold text-muted small text-uppercase mt-5 mb-3">Settings</h6>
                <div class="sidebar-item">
                    <i class="bi bi-gear"></i> Portal Settings
                </div>
            </div>
        </div>

        <div class="col-lg-9">
            <div class="glass p-4 card-glass">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="fw-bold mb-0" style="color: var(--text-main)">My Learning Content</h4>
                    <div class="dropdown">
                        <button class="btn btn-light btn-sm dropdown-toggle rounded-pill px-3" type="button" data-bs-toggle="dropdown">
                            Recent First
                        </button>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty courses}">
                        <c:forEach var="c" items="${courses}">
                            <div class="course-item shadow-sm">
                                <div class="row align-items-center">
                                    <div class="col-md-2">
                                        <img src="${c.thumbnail != null ? c.thumbnail : 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80'}" class="img-fluid rounded-3" alt="Thumbnail">
                                    </div>
                                    <div class="col-md-6">
                                        <span class="badge bg-primary-light text-success mb-1 small">${c.category}</span>
                                        <h5 class="fw-bold mb-1">${c.title}</h5>
                                        <p class="text-muted small mb-0">Difficulty: ${c.difficulty}</p>
                                    </div>
                                    <div class="col-md-4 text-md-end">
                                        <div class="d-flex justify-content-md-end gap-2 mt-3 mt-md-0">
                                            <button class="btn btn-outline-success btn-sm rounded-pill px-3"><i class="bi bi-pencil me-1"></i> Edit</button>
                                            <button class="btn btn-outline-primary btn-sm rounded-pill px-3"><i class="bi bi-eye me-1"></i> View</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <div class="bg-light p-4 rounded-circle d-inline-flex mb-3">
                                <i class="bi bi-journal-plus display-4 text-muted"></i>
                            </div>
                            <h5 class="fw-bold">No courses yet</h5>
                            <p class="text-muted small mb-4">Start sharing your knowledge by creating your first course.</p>
                            <button class="btn btn-gold rounded-pill px-4" data-bs-toggle="modal" data-bs-target="#importModal">Create First Course</button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Import Modal -->
<div class="modal fade" id="importModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 rounded-4 shadow-lg">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold">Import YouTube Playlist</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <p class="text-muted small mb-4">Convert a YouTube playlist into a structured course automatically.</p>
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted">Playlist Title</label>
                    <input type="text" id="courseTitle" class="form-control rounded-3" placeholder="e.g. Intro to Digital Literacy" required>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted">Description</label>
                    <textarea id="courseDescription" class="form-control rounded-3" rows="3" placeholder="What will students learn in this course?"></textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold">YouTube Playlist URL</label>
                    <input type="url" id="playlistUrl" class="form-control rounded-3" placeholder="https://youtube.com/playlist?list=...">
                </div>
                <div class="row g-3 mb-4">
                    <div class="col-6">
                        <label class="form-label small fw-bold">Category</label>
                        <select id="courseCategory" class="form-select rounded-3">
                            <option value="IT">IT & Software</option>
                            <option value="AGRI">Agriculture</option>
                            <option value="BIZ">Business</option>
                            <option value="EDU">Education</option>
                        </select>
                    </div>
                    <div class="col-6">
                        <label class="form-label small fw-bold">Difficulty</label>
                        <select id="courseDifficulty" class="form-select rounded-3">
                            <option value="Beginner">Beginner</option>
                            <option value="Intermediate">Intermediate</option>
                            <option value="Advanced">Advanced</option>
                        </select>
                    </div>
                </div>
                <button type="button" onclick="importCourse()" class="btn btn-gold w-100 py-3 rounded-pill fw-bold">
                    <i class="bi bi-cloud-arrow-down me-2"></i>Start Importing
                </button>
                <div id="importStatus" class="mt-3 text-center small d-none"></div>
            </div>
        </div>
    </div>
</div>

<footer class="py-5 mt-5" style="background: var(--text-main); color: rgba(255,255,255,0.7);">
    <div class="container text-center">
        <p class="small mb-0">&copy; 2026 RuralEduHub Mentor Portal. Empowering Rural India.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    async function importCourse() {
        const title = document.getElementById('courseTitle').value;
        const description = document.getElementById('courseDescription').value;
        const playlistUrl = document.getElementById('playlistUrl').value;
        const category = document.getElementById('courseCategory').value;
        const difficulty = document.getElementById('courseDifficulty').value;
        const statusDiv = document.getElementById('importStatus');

        if(!title || !playlistUrl) {
            alert('Please fill in all fields');
            return;
        }

        statusDiv.classList.remove('d-none');
        statusDiv.innerHTML = '<div class="spinner-border spinner-border-sm text-success me-2" role="status"></div> Importing...';

        try {
            const response = await fetch('/api/admin/courses/import', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title, playlistUrl, category, difficulty, description })
            });

            if(response.ok) {
                statusDiv.innerHTML = '<span class="text-success"><i class="bi bi-check-circle-fill me-1"></i> Course imported successfully! Reloading...</span>';
                setTimeout(() => window.location.reload(), 1500);
            } else {
                const data = await response.json();
                statusDiv.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle-fill me-1"></i> ' + (data.message || 'Error importing course') + '</span>';
            }
        } catch (error) {
            statusDiv.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle-fill me-1"></i> Network error</span>';
        }
    }
</script>
</body>
</html>
