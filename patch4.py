# 4. Student Course Enhancements
with open('./src/main/webapp/WEB-INF/jsp/student/course_view.jsp', 'r') as f:
    content = f.read()

target = """                    <c:otherwise>
                        <div class="video-container mb-4 shadow-lg" style="aspect-ratio: 16/9; background: #000; border-radius: 20px; overflow: hidden;">
                            <iframe id="main-player" width="100%" height="100%"
                                    src="https://www.youtube.com/embed/${not empty course.lessons ? course.lessons[0].videoId : ''}"
                                    frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                    allowfullscreen></iframe>
                        </div>
                    </c:otherwise>"""

replacement = """                    <c:otherwise>
                        <c:choose>
                            <c:when test="${empty course.lessons}">
                                <div class="video-container mb-4 shadow-lg d-flex align-items-center justify-content-center" style="aspect-ratio: 16/9; background: #222; border-radius: 20px;">
                                    <div class="text-center text-white">
                                        <i class="bi bi-camera-video-off fs-1 mb-3"></i>
                                        <h5 class="fw-bold">No Content Available</h5>
                                        <p class="small text-muted mb-0">Lessons for this course will be added soon.</p>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="video-container mb-4 shadow-lg" style="aspect-ratio: 16/9; background: #000; border-radius: 20px; overflow: hidden;">
                                    <iframe id="main-player" width="100%" height="100%"
                                            src="https://www.youtube.com/embed/${course.lessons[0].videoId}"
                                            frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                            allowfullscreen></iframe>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("WARNING: Target 1 not found")

target2 = """                <div class="mt-5 p-4 rounded-4 text-center" style="background: var(--secondary); color: white;">
                    <i class="bi bi-lightning-charge-fill fs-2 mb-2"></i>
                    <h6 class="fw-bold">Ready to take the quiz?</h6>
                    <p class="small opacity-75">Pass with 80% to earn 50 points!</p>
                    <a href="/student/course/${course.id}/quiz" class="btn btn-light w-100 py-2 rounded-pill fw-bold text-success">Go to Quiz</a>
                </div>"""

replacement2 = """                <c:if test="${progress == 100}">
                    <div class="mt-4 p-4 rounded-4 text-center shadow" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white;">
                        <i class="bi bi-award fs-1 mb-2"></i>
                        <h6 class="fw-bold">Congratulations!</h6>
                        <p class="small opacity-75 mb-3">You have completed this course.</p>
                        <a href="/api/certificates/${course.id}/download" class="btn btn-light w-100 py-2 rounded-pill fw-bold text-success">
                            <i class="bi bi-download me-2"></i> Download Certificate
                        </a>
                    </div>
                </c:if>
                <div class="mt-5 p-4 rounded-4 text-center" style="background: var(--secondary); color: white;">
                    <i class="bi bi-lightning-charge-fill fs-2 mb-2"></i>
                    <h6 class="fw-bold">Ready to take the quiz?</h6>
                    <p class="small opacity-75">Pass with 80% to earn 50 points!</p>
                    <a href="/student/course/${course.id}/quiz" class="btn btn-light w-100 py-2 rounded-pill fw-bold text-success">Go to Quiz</a>
                </div>"""

if target2 in content:
    content = content.replace(target2, replacement2)
else:
    print("WARNING: Target 2 not found")

with open('./src/main/webapp/WEB-INF/jsp/student/course_view.jsp', 'w') as f:
    f.write(content)
