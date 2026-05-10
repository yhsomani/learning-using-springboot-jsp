<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Command Center - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        :root {
            --admin-sidebar: #064e3b;
        }
        body { background-color: #f8fafc; }
        .sidebar-admin {
            background-color: var(--admin-sidebar);
            min-height: 100vh;
            color: white;
            padding: 30px 15px;
        }
        .nav-link-admin {
            color: rgba(255, 255, 255, 0.7);
            padding: 12px 20px;
            border-radius: 10px;
            margin-bottom: 5px;
            display: flex;
            align-items: center;
            gap: 12px;
            text-decoration: none;
            transition: var(--transition);
        }
        .nav-link-admin:hover, .nav-link-admin.active {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 20px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }
        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-lg-2 sidebar-admin d-none d-lg-block">
                <div class="d-flex align-items-center mb-5 px-3">
                    <i class="bi bi-shield-lock-fill fs-3 text-primary me-2"></i>
                    <span class="fs-4 fw-800 text-primary">Admin</span>
                </div>
                
                <nav>
                    <div class="text-white-50 small fw-bold mb-3 px-3">OVERVIEW</div>
                    <a href="#overview" class="nav-link-admin active"><i class="bi bi-grid-1x2"></i> Dashboard</a>
                    <a href="#user-management" class="nav-link-admin"><i class="bi bi-people"></i> User Management</a>
                    <a href="/admin/dashboard" class="nav-link-admin"><i class="bi bi-book"></i> Courses</a>
                    
                    <div class="text-white-50 small fw-bold mt-4 mb-3 px-3">IMPACT</div>
                    <a href="#sdg-metrics" class="nav-link-admin"><i class="bi bi-globe"></i> SDG Metrics</a>
                    <a href="/admin/dashboard" class="nav-link-admin"><i class="bi bi-award"></i> Certificates</a>
                    
                    <div class="text-white-50 small fw-bold mt-4 mb-3 px-3">SYSTEM</div>
                    <a href="#system-alerts" class="nav-link-admin"><i class="bi bi-gear"></i> Settings</a>
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link-admin text-danger"><i class="bi bi-box-arrow-right"></i> Logout</a>
                </nav>
            </div>

            <!-- Main Content -->
            <div class="col-lg-10 p-lg-5 p-4" id="overview">
                <div class="d-flex justify-content-between align-items-center mb-5">
                    <div>
                        <h1 class="fw-bold mb-1">Command Center</h1>
                        <p class="text-muted mb-0">Platform-wide statistics and management</p>
                    </div>
                    <a href="/admin/users/export" class="btn btn-white shadow-sm border-0 rounded-pill px-4"><i class="bi bi-download me-2"></i>Export Report</a>
                </div>

                <!-- Stats Grid -->
                <div class="row g-4 mb-5">
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-primary-subtle text-primary">
                                <i class="bi bi-people-fill"></i>
                            </div>
                            <h6 class="text-muted mb-1">Total Scholars</h6>
                            <h3 class="fw-bold mb-0">${totalUsers}</h3>
                            <div class="text-success small mt-2">
                                <i class="bi bi-graph-up-arrow"></i> 12% from last month
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-info-subtle text-info">
                                <i class="bi bi-journal-bookmark-fill"></i>
                            </div>
                            <h6 class="text-muted mb-1">Total Courses</h6>
                            <h3 class="fw-bold mb-0">${totalCourses}</h3>
                            <div class="text-primary small mt-2">
                                <i class="bi bi-plus-circle-fill"></i> 3 new this week
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-warning-subtle text-warning">
                                <i class="bi bi-mortarboard-fill"></i>
                            </div>
                            <h6 class="text-muted mb-1">Total Enrollments</h6>
                            <h3 class="fw-bold mb-0">${totalEnrollments}</h3>
                            <div class="text-muted small mt-2">
                                ${String.format("%.1f", sdgMetrics.completionRate)}% completion rate
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-danger-subtle text-danger">
                                <i class="bi bi-heart-pulse-fill"></i>
                            </div>
                            <h6 class="text-muted mb-1">SDG 4 Impact</h6>
                            <h3 class="fw-bold mb-0">${String.format("%.1f", sdgMetrics.completionRate)}%</h3>
                            <div class="text-danger small mt-2">
                                <i class="bi bi-check2-circle"></i> Target Achieved
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row g-4">
                    <!-- User Management Preview -->
                    <div class="col-md-8" id="user-management">
                        <div class="card border-0 shadow-sm rounded-4 p-4">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h5 class="fw-bold mb-0">Recent Scholar Activity</h5>
                                <div id="bulk-actions" class="d-none animate-fade">
                                    <button class="btn btn-outline-danger btn-sm rounded-pill px-3" onclick="bulkDelete()"><i class="bi bi-trash me-1"></i> Bulk Delete</button>
                                </div>
                            </div>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle" id="userTable">
                                    <thead class="table-light">
                                        <tr>
                                            <th style="width: 40px;"><input type="checkbox" class="form-check-input" id="selectAll"></th>
                                            <th>Scholar</th>
                                            <th>Role</th>
                                            <th>Location</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="u" items="${recentUsers}">
                                            <tr id="row-${u.id}">
                                                <td><input type="checkbox" class="form-check-input user-checkbox" data-id="${u.id}"></td>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <div class="bg-light rounded-circle me-3 d-flex align-items-center justify-content-center fw-bold" style="width: 40px; height: 40px;">
                                                            ${u.fullName.substring(0,1)}
                                                        </div>
                                                        <div>
                                                            <div class="fw-bold small">${u.fullName}</div>
                                                            <div class="text-muted x-small">${u.email}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td><span class="badge bg-light text-dark border">${u.role}</span></td>
                                                <td>${u.location != null ? u.location : 'N/A'}</td>
                                                <td>
                                                    <span class="badge ${u.enabled ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} rounded-pill px-3">
                                                        ${u.enabled ? 'Active' : 'Disabled'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="dropdown">
                                                        <button class="btn btn-light btn-sm rounded-circle" type="button" data-bs-toggle="dropdown">
                                                            <i class="bi bi-three-dots"></i>
                                                        </button>
                                                        <ul class="dropdown-menu dropdown-menu-end border-0 shadow-sm rounded-3">
                                                            <li><a class="dropdown-item" href="javascript:void(0)" onclick="editUser(${u.id})"><i class="bi bi-pencil me-2"></i> Edit Details</a></li>
                                                            <li><a class="dropdown-item" href="javascript:void(0)" onclick="toggleUserStatus(${u.id})"><i class="bi ${u.enabled ? 'bi-person-x' : 'bi-person-check'} me-2"></i> ${u.enabled ? 'Disable' : 'Enable'} User</a></li>
                                                            <li><hr class="dropdown-divider"></li>
                                                            <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="deleteUser(${u.id})"><i class="bi bi-trash me-2"></i> Delete Account</a></li>
                                                        </ul>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            
                            <!-- Pagination Controls -->
                            <c:if test="${totalPages > 1}">
                                <nav class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                            <a class="page-link rounded-start-pill px-3" href="?page=${currentPage - 1}">Previous</a>
                                        </li>
                                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="?page=${i}">${i + 1}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                                            <a class="page-link rounded-end-pill px-3" href="?page=${currentPage + 1}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </div>
                    </div>

                    <!-- Platform Health -->
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm rounded-4 p-4 mb-4" id="sdg-metrics">
                            <h5 class="fw-bold mb-4">SDG Goal Progress</h5>
                            <canvas id="sdgChart" height="200"></canvas>
                        </div>
                        <div class="card border-0 shadow-sm rounded-4 p-4 bg-primary text-white" id="system-alerts">
                            <h5 class="fw-bold mb-3">System Alerts</h5>
                            <c:forEach var="alert" items="${systemAlerts}">
                                <div class="d-flex gap-3 mb-3">
                                    <i class="bi ${alert.icon}"></i>
                                    <div>
                                        <div class="fw-bold">${alert.title}</div>
                                        <div class="small opacity-75">${alert.description}</div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div class="card border-0 shadow-sm rounded-4 p-4 mt-4">
                            <h5 class="fw-bold mb-4">Admin Audit Logs</h5>
                            <div class="small">
                                <c:forEach var="log" items="${auditLogs}">
                                    <div class="d-flex gap-3 mb-3 border-bottom pb-2">
                                        <div class="text-primary fw-bold" style="min-width: 100px;">${log.action}</div>
                                        <div>
                                            <div class="fw-bold">${log.performedBy}</div>
                                            <div class="text-muted x-small">${log.details}</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit User Modal -->
    <div class="modal fade" id="editUserModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 rounded-4 shadow-lg">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold">Edit User Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <form id="editUserForm">
                        <input type="hidden" id="editUserId">
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Full Name</label>
                            <input type="text" id="editFullName" class="form-control rounded-3" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Email Address</label>
                            <input type="email" id="editEmail" class="form-control rounded-3" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label small fw-bold">System Role</label>
                            <select id="editRole" class="form-select rounded-3">
                                <option value="STUDENT">Student</option>
                                <option value="TEACHER">Teacher</option>
                                <option value="PARENT">Parent</option>
                                <option value="ADMIN">Administrator</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-3 rounded-pill fw-bold">Save Changes</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const ctx = document.getElementById('sdgChart').getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Completed', 'In Progress'],
                datasets: [{
                    data: [${sdgMetrics.completionRate}, ${sdgMetrics.inProgressRate}],
                    backgroundColor: ['#fff', 'rgba(255,255,255,0.3)'],
                    borderColor: 'transparent',
                    hoverOffset: 4
                }]
            },
            options: {
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { color: '#000' }
                    }
                }
            }
        });

        const editModal = new bootstrap.Modal(document.getElementById('editUserModal'));

        async function editUser(userId) {
            try {
                const response = await fetch('/admin/users/' + userId);
                if (response.ok) {
                    const user = await response.json();
                    document.getElementById('editUserId').value = user.id;
                    document.getElementById('editFullName').value = user.fullName;
                    document.getElementById('editEmail').value = user.email;
                    document.getElementById('editRole').value = user.role;
                    editModal.show();
                }
            } catch (error) {
                console.error('Failed to fetch user', error);
            }
        }

        document.getElementById('editUserForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('editUserId').value;
            const data = {
                fullName: document.getElementById('editFullName').value,
                email: document.getElementById('editEmail').value,
                role: document.getElementById('editRole').value
            };

            try {
                const response = await fetch('/admin/users/' + id + '/update', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                if (response.ok) {
                    window.location.reload();
                }
            } catch (error) {
                console.error('Failed to update user', error);
            }
        });

        async function toggleUserStatus(userId) {
            if (confirm('Are you sure you want to change this user\'s status?')) {
                try {
                    const response = await fetch('/admin/users/' + userId + '/toggle-status', { method: 'POST' });
                    if (response.ok) {
                        window.location.reload();
                    }
                } catch (error) {
                    console.error('Failed to toggle status', error);
                }
            }
        }

        async function deleteUser(userId) {
            if (confirm('PERMANENT ACTION: Are you sure you want to delete this account? This cannot be undone.')) {
                try {
                    const response = await fetch('/admin/users/' + userId, { method: 'DELETE' });
                    if (response.ok) {
                        window.location.reload();
                    }
                } catch (error) {
                    console.error('Failed to delete user', error);
                }
            }
        }

        // Bulk Selection Logic
        const selectAll = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.user-checkbox');
        const bulkActions = document.getElementById('bulk-actions');

        selectAll.addEventListener('change', (e) => {
            checkboxes.forEach(cb => cb.checked = e.target.checked);
            updateBulkVisibility();
        });

        checkboxes.forEach(cb => {
            cb.addEventListener('change', updateBulkVisibility);
        });

        function updateBulkVisibility() {
            const anyChecked = Array.from(checkboxes).some(cb => cb.checked);
            if (anyChecked) {
                bulkActions.classList.remove('d-none');
            } else {
                bulkActions.classList.add('d-none');
            }
        }

        async function bulkDelete() {
            const selectedIds = Array.from(checkboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.getAttribute('data-id'));
            
            if (confirm(`Are you sure you want to delete ${selectedIds.length} users?`)) {
                try {
                    const response = await fetch('/admin/users/bulk-delete', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ ids: selectedIds })
                    });
                    if (response.ok) {
                        window.location.reload();
                    }
                } catch (error) {
                    console.error('Bulk delete failed', error);
                }
            }
        }
    </script>
</body>
</html>
