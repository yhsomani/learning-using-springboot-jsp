<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Courses - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Course Management</h2>
    <a href="/admin/dashboard" class="btn btn-secondary mb-3">Back to Dashboard</a>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Category</th>
            <th>Difficulty</th>
            <th>Teacher</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="course" items="${courses}">
            <tr>
                <td>${course.id}</td>
                <td>${course.title}</td>
                <td>${course.category}</td>
                <td>${course.difficulty}</td>
                <td>${course.teacher.fullName}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
