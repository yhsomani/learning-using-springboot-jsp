<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Student Dashboard - RuralEduHub</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
            rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
        <link rel="stylesheet" href="/css/global.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            .dashboard-container {
                padding: 40px 0;
            }

            .stat-card {
                padding: 25px;
                margin-bottom: 25px;
            }

            .course-card {
                transition: var(--transition);
                border-radius: 20px;
                overflow: hidden;
            }

            .progress-compact {
                height: 6px;
                border-radius: 3px;
            }

            .sidebar-card {
                position: sticky;
                top: 100px;
            }

            .badge-point {
                background: var(--secondary);
                color: white;
                padding: 5px 12px;
                border-radius: 20px;
                font-weight: 700;
            }
        </style>
    </head>

    <body>

        <nav class="navbar navbar-expand-lg navbar-custom">
            <div class="container">
                <a class="navbar-brand" href="/student/dashboard">
                    <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
                </a>
                <div class="ms-auto d-flex align-items-center">
                    <div class="form-check form-switch me-4 text-white">
                        <input class="form-check-input" type="checkbox" id="lowBandwidthToggle" ${sessionScope.lowBandwidthMode ? 'checked' : ''}>
                        <label class="form-check-label small" for="lowBandwidthToggle">Low Bandwidth</label>
                    </div>
                    <div class="me-4 d-none d-md-block">
                        <span class="text-white opacity-75 small">Logged in as</span>
                        <div class="text-white fw-bold">${user.fullName}</div>
                    </div>
                    <div class="badge-point me-3">
                        <i class="bi bi-gem me-1"></i>${user.points}
                    </div>
                    <a href="/logout" class="btn btn-outline-light btn-sm rounded-pill px-3">Logout</a>
                </div>
            </div>
        </nav>

        <div class="container dashboard-container">
            <div class="row">
                <div class="col-lg-8 animate-fade">
                    <div class="d-flex align-items-center justify-content-between mb-4">
                        <h2 class="fw-bold" style="color: var(--text-main)">My Learning Journey</h2>
                        <div class="d-flex gap-2">
                            <form action="/student/courses" method="GET" class="d-none d-md-flex">
                                <div class="input-group">
                                    <input type="text" name="search" class="form-control border-0 shadow-sm px-3" placeholder="Search courses..." style="border-radius: 20px 0 0 20px; width: 250px;">
                                    <button class="btn btn-white border-0 shadow-sm px-3" type="submit" style="border-radius: 0 20px 20px 0;">
                                        <i class="bi bi-search text-primary"></i>
                                    </button>
                                </div>
                            </form>
                            <a href="/student/courses" class="btn-gold px-3 py-2 small" style="font-size: 0.85rem;">Browse All</a>
                        </div>
                    </div>

                    <div class="row">
                        <c:if test="${empty enrollments}">
                            <div class="col-12">
                                <div class="glass p-5 text-center">
                                    <i class="bi bi-book display-1 text-muted opacity-25"></i>
                                    <h4 class="mt-3 text-muted">No enrollments yet</h4>
                                    <p>Start your learning journey by enrolling in a course!</p>
                                    <a href="/student/courses" class="btn-verdant mt-2">Explore Courses</a>
                                </div>
                            </div>
                        </c:if>
                        <c:forEach var="e" items="${enrollments}">
                            <div class="col-md-6 mb-4">
                                <div class="glass course-card card-glass h-100">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <span
                                            class="badge bg-primary-light text-success rounded-pill px-3 py-2 small fw-bold">
                                            ${e.course.category}
                                        </span>
                                        <div class="text-secondary fw-bold">${e.progress}%</div>
                                    </div>
                                    <h4 class="h5 fw-bold mb-3">${e.course.title}</h4>
                                    <div class="progress progress-compact mb-3">
                                        <c:set var="currentProgress" value="${e.progress}%" />
                                        <div class="progress-bar" role="progressbar"
                                            style="width: ${currentProgress}; background: var(--primary);"
                                            aria-valuenow="${e.progress}" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mt-auto">
                                        <span class="small text-muted"><i class="bi bi-clock me-1"></i> Active</span>
                                        <a href="/student/course/${e.course.id}" class="btn-verdant px-3 py-2 small"
                                            style="font-size: 0.8rem;">Continue</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 class="fw-bold mt-5 mb-4" style="color: var(--text-main)">My Certificates</h3>
                    <div class="row">
                        <c:if test="${empty certificates}">
                            <div class="col-12">
                                <div class="glass p-4 text-center border-dashed">
                                    <p class="text-muted mb-0 small">Complete courses and pass quizzes to earn certificates!</p>
                                </div>
                            </div>
                        </c:if>
                        <c:forEach var="cert" items="${certificates}">
                            <div class="col-md-6 mb-4">
                                <div class="glass p-4 card-glass border-0" style="border-left: 4px solid var(--primary) !important;">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="bg-success bg-opacity-10 p-3 rounded-3 me-3 text-success">
                                            <i class="bi bi-patch-check-fill fs-4"></i>
                                        </div>
                                        <div>
                                            <h5 class="fw-bold mb-0">${cert.course.title}</h5>
                                            <span class="small text-muted">Issued on ${cert.issuedDate}</span>
                                        </div>
                                    </div>
                                    <a href="/api/certificates/${cert.course.id}/download" class="btn btn-outline-success w-100 py-2 text-center small">
                                        <i class="bi bi-download me-2"></i>Download PDF
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 class="fw-bold mt-5 mb-4" style="color: var(--text-main)">New Arrivals</h3>
                    <div class="row">
                        <c:forEach var="c" items="${newArrivals}">
                            <div class="col-md-6 mb-4">
                                <div class="glass p-4 card-glass border-0"
                                    style="border-left: 4px solid var(--primary) !important;">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="bg-primary-light p-3 rounded-3 me-3">
                                            <i class="bi bi-rocket-takeoff text-success fs-4"></i>
                                        </div>
                                        <div>
                                            <h5 class="fw-bold mb-0">${c.title}</h5>
                                            <span class="small text-muted">${c.category}</span>
                                        </div>
                                    </div>
                                    <p class="small text-muted mb-4">
                                        <c:choose>
                                            <c:when test="${c.description.length() > 100}">
                                                ${c.description.substring(0, 100)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${c.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <a href="/payments/checkout/${c.id}"
                                        class="btn-verdant w-100 py-2 text-center small">Enroll Now</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 class="fw-bold mt-5 mb-4" style="color: var(--text-main)">Picks for You</h3>
                    <div class="row">
                        <c:if test="${empty recommendations}">
                            <div class="col-12">
                                <div class="glass p-4 text-center border-dashed">
                                    <p class="text-muted mb-0 small">Enroll in more courses to get personalized recommendations!</p>
                                </div>
                            </div>
                        </c:if>
                        <c:forEach var="c" items="${recommendations}">
                            <div class="col-md-6 mb-4">
                                <div class="glass p-4 card-glass border-0"
                                    style="border-left: 4px solid var(--secondary) !important;">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="bg-primary-light p-3 rounded-3 me-3">
                                            <i class="bi bi-stars text-success fs-4"></i>
                                        </div>
                                        <div>
                                            <h5 class="fw-bold mb-0">${c.title}</h5>
                                            <span class="small text-muted">${c.category}</span>
                                        </div>
                                    </div>
                                    <p class="small text-muted mb-4">
                                        <c:choose>
                                            <c:when test="${c.description.length() > 100}">
                                                ${c.description.substring(0, 100)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${c.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <a href="/payments/checkout/${c.id}"
                                        class="btn-gold w-100 py-2 text-center small">Enroll Now</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="col-lg-4 animate-fade" style="animation-delay: 0.2s;">
                    <div class="sidebar-card">
                        <div class="glass stat-card mb-4">
                            <h5 class="fw-bold mb-4"><i class="bi bi-graph-up-arrow me-2 text-success"></i>SDG Impact
                            </h5>
                            <div style="height: 200px;">
                                <canvas id="sdgChart"></canvas>
                            </div>
                            <div class="text-center mt-3 small text-muted">
                                You've contributed <strong>${user.points} points</strong> towards ${sdgImpact.goal}.
                            </div>
                        </div>

                        <div class="glass p-4 card-glass">
                            <h5 class="fw-bold mb-4"><i class="bi bi-trophy me-2 text-warning"></i>Top Scholars</h5>
                            <div class="leaderboard-list">
                                <c:forEach var="topUser" items="${leaderboard}" varStatus="status">
                                    <div class="d-flex align-items-center mb-3 ${topUser.id == user.id ? 'p-2 rounded-3' : ''}"
                                         style="${topUser.id == user.id ? 'background: var(--primary-light);' : ''}">
                                        <div class="text-white rounded-circle d-flex align-items-center justify-content-center me-3"
                                            style="width: 35px; height: 35px; font-weight: 800; background: ${status.index == 0 ? '#fbbf24' : (status.index == 1 ? '#cbd5e1' : (status.index == 2 ? '#d97706' : '#94a3b8'))} !important;">
                                            ${status.index + 1}
                                        </div>
                                        <div class="flex-grow-1">
                                            <div class="fw-bold small">${topUser.id == user.id ? 'You' : topUser.fullName}</div>
                                            <div class="text-muted" style="font-size: 0.7rem;">${topUser.location != null ? topUser.location : 'Rural Scholar'}</div>
                                        </div>
                                        <div class="fw-bold text-success small">${topUser.points} pts</div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <footer class="py-5 mt-5" style="background: var(--text-main); color: rgba(255,255,255,0.7);">
            <div class="container text-center">
                <p class="small mb-0">&copy; 2026 RuralEduHub. Powering the next generation of rural talent.</p>
            </div>
        </footer>

        <script>
            const ctx = document.getElementById('sdgChart').getContext('2d');
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Your Progress', 'Target'],
                    <c:set var="userPoints" value="${user.points > 0 ? user.points : 10}" />
                    datasets: [{
                        data: [${userPoints}, 1000],
                        backgroundColor: ['#10b981', 'rgba(16, 185, 129, 0.1)'],
                        borderWidth: 0,
                        hoverOffset: 4
                    }]
                },
                options: {
                    cutout: '80%',
                    plugins: {
                        legend: { display: false },
                        tooltip: { enabled: true }
                    },
                    maintainAspectRatio: false
                }
            });
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('lowBandwidthToggle')?.addEventListener('change', async (e) => {
                const enabled = e.target.checked;
                try {
                    await fetch('/api/settings/low-bandwidth', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ enabled })
                    });
                    window.location.reload();
                } catch (err) {
                    console.error('Failed to toggle bandwidth mode', err);
                }
            });
        </script>
    </body>

    </html>