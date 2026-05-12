<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${course.title} - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <style>
        .course-hero {
            padding: 80px 0;
            background: linear-gradient(135deg, #064e3b 0%, #10b981 100%);
            color: white;
            border-radius: 0 0 50px 50px;
        }
        .content-glass {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border-radius: 25px;
            border: 1px solid rgba(255, 255, 255, 0.4);
            padding: 40px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.05);
        }
        .video-placeholder {
            aspect-ratio: 16/9;
            background: #000;
            border-radius: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 30px;
            overflow: hidden;
            position: relative;
        }
        .play-btn {
            width: 80px;
            height: 80px;
            background: var(--primary);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            cursor: pointer;
            transition: var(--transition);
            z-index: 2;
        }
        .play-btn:hover {
            transform: scale(1.1);
            background: var(--secondary);
        }
        .sidebar-item {
            padding: 15px;
            border-radius: 15px;
            transition: var(--transition);
            cursor: pointer;
            border: 1px solid transparent;
        }
        .sidebar-item:hover {
            background: #f0fdf4;
            border-color: #d1fae5;
        }
        .sidebar-item.active {
            background: var(--primary-light);
            border-color: var(--primary);
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-custom sticky-top">
    <div class="container">
        <a class="navbar-brand" href="/student/dashboard">
            <i class="bi bi-mortarboard-fill me-2"></i>RuralEduHub
        </a>
        <div class="ms-auto d-flex align-items-center">
            <div class="form-check form-switch me-4 text-white">
                <input class="form-check-input" type="checkbox" id="lowBandwidthToggle" ${sessionScope.lowBandwidthMode ? 'checked' : ''}>
                <label class="form-check-label small" for="lowBandwidthToggle">Low Bandwidth</label>
            </div>
            <a href="/student/courses" class="text-white text-decoration-none me-4 small">
                <i class="bi bi-arrow-left me-1"></i> Back to Courses
            </a>
            <a href="/logout" class="btn btn-outline-light btn-sm rounded-pill px-3">Logout</a>
        </div>
    </div>
</nav>

<header class="course-hero">
    <div class="container animate-fade">
        <div class="row align-items-center">
            <div class="col-lg-8">
                <nav aria-label="breadcrumb" class="mb-4">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="/student/dashboard" class="text-white-50 small">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="/student/courses" class="text-white-50 small">Courses</a></li>
                        <li class="breadcrumb-item active text-white small" aria-current="page">${course.title}</li>
                    </ol>
                </nav>
                <span class="badge bg-white text-success px-3 py-2 rounded-pill small fw-bold mb-3">${course.category}</span>
                <h1 class="display-4 fw-bold mb-3">${course.title}</h1>
                <div class="d-flex align-items-center gap-4">
                    <div class="small"><i class="bi bi-bar-chart-steps me-1"></i> ${course.difficulty}</div>
                    <div class="small"><i class="bi bi-people me-1"></i> ${enrollmentCount} Scholars</div>
                </div>
            </div>
        </div>
    </div>
</header>

<div class="container py-5 mt-n5">
    <div class="row g-5">
        <div class="col-lg-8 animate-fade" style="animation-delay: 0.1s;">
            <div class="content-glass mb-4">
                <c:choose>
                    <c:when test="${textOnlyMode}">
                        <div class="alert alert-info border-0 rounded-4 p-4 mb-4">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-info-circle-fill fs-2 me-3"></i>
                                <div>
                                    <h6 class="fw-bold mb-1">Low Bandwidth Mode Active</h6>
                                    <p class="small mb-0 opacity-75">Video content and high-resolution images are hidden to save data. Toggle off to view lessons.</p>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
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
                    </c:otherwise>
                </c:choose>
                
                <h3 class="fw-bold mb-4" style="color: var(--text-main)">Course Overview</h3>
                <p class="text-muted lead mb-4">${course.description}</p>
                
                <div class="row g-4 mt-2">
                    <div class="col-md-6">
                        <div class="p-3 rounded-4 bg-light">
                            <h6 class="fw-bold mb-2"><i class="bi bi-check2-circle me-2 text-success"></i>What you'll learn</h6>
                            <ul class="small text-muted ps-3 mb-0">
                                <li>Foundational concepts and principles</li>
                                <li>Real-world applications in rural context</li>
                                <li>Interactive problem solving</li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 rounded-4 bg-light">
                            <h6 class="fw-bold mb-2"><i class="bi bi-award me-2 text-warning"></i>Earn a Certificate</h6>
                            <p class="small text-muted mb-0">Complete all modules and pass the final quiz to receive your RuralEduHub verified certificate.</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="content-glass">
                <h4 class="fw-bold mb-4" style="color: var(--text-main)">Quizzes & Assessments</h4>
                <c:choose>
                    <c:when test="${not empty course.quiz}">
                        <div class="d-flex flex-column gap-3">
                            <div class="p-4 rounded-4 border border-opacity-10 d-flex justify-content-between align-items-center hover-lift">
                                <div class="d-flex align-items-center">
                                    <div class="bg-primary-light p-3 rounded-3 me-3 text-success">
                                        <i class="bi bi-patch-question fs-4"></i>
                                    </div>
                                    <div>
                                        <h6 class="fw-bold mb-1">${course.quiz.title}</h6>
                                        <span class="small text-muted">10 Questions • 15 Minutes</span>
                                    </div>
                                </div>
                                <a href="/student/course/${course.id}/quiz" class="btn-gold px-4 py-2 text-decoration-none">Start Quiz</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <i class="bi bi-journal-x display-4 text-muted opacity-25"></i>
                            <p class="text-muted mt-3">No assessments available for this course yet.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="col-lg-4 animate-fade" style="animation-delay: 0.2s;">
            <div class="content-glass sticky-top" style="top: 100px;">
                <h5 class="fw-bold mb-4">Course Content</h5>
                <div class="d-flex flex-column gap-2" id="lesson-list">
                    <c:forEach var="lesson" items="${course.lessons}" varStatus="status">
                        <div class="sidebar-item d-flex align-items-center ${status.first ? 'active' : ''}" 
                             onclick="playLesson(${lesson.id}, '${lesson.videoId}', this)" 
                             style="cursor: pointer;"
                             id="lesson-${lesson.id}">
                            <i class="bi ${completedLessonIds.contains(lesson.id) ? 'bi-check-circle-fill text-success' : 'bi-play-circle text-muted'} me-3 fs-5"></i>
                            <div class="flex-grow-1">
                                <div class="fw-bold small">${status.index + 1}. ${lesson.title}</div>
                                <span class="small text-muted" style="font-size: 0.7rem;">${lesson.duration != null ? lesson.duration : '05:00'} mins</span>
                            </div>
                            <div class="ms-2" onclick="event.stopPropagation(); markComplete(${lesson.id}, this.parentElement)">
                                <i class="bi ${completedLessonIds.contains(lesson.id) ? 'bi-patch-check-fill text-success' : 'bi-patch-check text-muted'} hover-lift"></i>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty course.lessons}">
                        <div class="text-center py-3">
                            <p class="small text-muted mb-0">No lessons available yet.</p>
                        </div>
                    </c:if>
                </div>
                
                <c:if test="${progress == 100}">
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
                </div>
            </div>
        </div>
    </div>
</div>

<div id="chat-widget" class="position-fixed bottom-0 end-0 m-4 animate-fade" style="z-index: 1000; animation-delay: 0.5s;">
    <button id="chat-toggle" class="btn btn-gold rounded-circle shadow-lg d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
        <i class="bi bi-chat-dots-fill fs-3"></i>
    </button>
    
    <div id="chat-window" class="glass card-glass p-0 overflow-hidden d-none shadow-lg" style="width: 350px; height: 450px; position: absolute; bottom: 80px; right: 0; border: 1px solid rgba(16, 185, 129, 0.2);">
        <div class="p-3 bg-success text-white d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
                <div class="bg-white rounded-circle me-2 d-flex align-items-center justify-content-center text-success fw-bold" style="width: 30px; height: 30px;">M</div>
                <h6 class="mb-0 fw-bold">Mentor Support</h6>
            </div>
            <button id="close-chat" class="btn-close btn-close-white small"></button>
        </div>
        <div id="chat-messages" class="p-3 overflow-y-auto" style="height: 330px; background: rgba(255,255,255,0.5);">
            <div class="mb-3">
                <div class="bg-white p-2 rounded-3 small shadow-sm d-inline-block" style="max-width: 80%;">
                    Hello ${user.fullName}! How can I help you with your studies today?
                </div>
            </div>
        </div>
        <div class="p-3 border-top bg-white">
            <div class="input-group">
                <input type="text" id="message-input" class="form-control form-control-sm border-0 bg-light" placeholder="Type your question...">
                <button id="send-btn" class="btn btn-success btn-sm px-3"><i class="bi bi-send-fill"></i></button>
            </div>
        </div>
    </div>
</div>

<footer class="py-5 mt-5" style="background: var(--text-main); color: rgba(255,255,255,0.7);">
    <div class="container text-center">
        <p class="small mb-0">&copy; 2026 RuralEduHub. Powered by Rural Innovation.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script>
    let stompClient = null;
    let progressInterval = null;
    let currentLessonId = null;
    let watchedSeconds = 0;

    function playLesson(lessonId, videoId, element) {
        if (!videoId) return;
        
        // Update player
        const player = document.getElementById('main-player');
        if (player) {
            player.src = 'https://www.youtube.com/embed/' + videoId + '?autoplay=1';
        }
        
        // Update UI
        document.querySelectorAll('.sidebar-item').forEach(el => el.classList.remove('active'));
        element.classList.add('active');
        
        // Start tracking
        startProgressTracking(lessonId);
    }

    function startProgressTracking(lessonId) {
        if (progressInterval) clearInterval(progressInterval);
        currentLessonId = lessonId;
        watchedSeconds = 0;
        
        progressInterval = setInterval(() => {
            watchedSeconds += 10;
            updateProgressOnServer(lessonId, watchedSeconds, false);
        }, 10000); // Ping every 10 seconds
    }

    async function updateProgressOnServer(lessonId, seconds, completed) {
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        try {
            await fetch('/api/lessons/' + lessonId + '/progress', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify({ 
                    watchedSeconds: seconds,
                    completed: completed
                })
            });
        } catch (error) {
            console.error('Progress sync failed:', error);
        }
    }

    function connect() {
        const socket = new SockJS('/ws-mentor-chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/messages', function (message) {
                showMessage(JSON.parse(message.body));
            });
        });
    }

    function sendMessage() {
        const input = document.getElementById('message-input');
        const content = input.value.trim();
        if (content && stompClient) {
            stompClient.send("/app/mentor-message", {}, JSON.stringify({
                'sender': '${user.fullName}',
                'content': content
            }));
            input.value = '';
        }
    }

    function showMessage(message) {
        const msgDiv = document.getElementById('chat-messages');
        const isMe = message.sender === '${user.fullName}';
        const html = '<div class="mb-3 ' + (isMe ? 'text-end' : '') + '">' +
                     '<div class="' + (isMe ? 'bg-success text-white' : 'bg-white') + ' p-2 rounded-3 small shadow-sm d-inline-block" style="max-width: 80%;">' +
                     '<div class="fw-bold" style="font-size: 0.65rem;">' + message.sender + '</div>' +
                     message.content + '</div></div>';
        msgDiv.innerHTML += html;
        msgDiv.scrollTop = msgDiv.scrollHeight;
    }

    document.getElementById('chat-toggle').addEventListener('click', () => {
        document.getElementById('chat-window').classList.toggle('d-none');
        if (!stompClient) connect();
    });

    document.getElementById('close-chat').addEventListener('click', () => {
        document.getElementById('chat-window').classList.add('d-none');
    });

    document.getElementById('send-btn').addEventListener('click', sendMessage);
    document.getElementById('message-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
    });

    async function markComplete(lessonId, element) {
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        try {
            const response = await fetch('/api/lessons/' + lessonId + '/complete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                }
            });
            if (response.ok) {
                const icon = element.querySelector('i.bi-play-circle');
                if (icon) {
                    icon.classList.remove('bi-play-circle', 'text-muted');
                    icon.classList.add('bi-check-circle-fill', 'text-success');
                }
                const checkIcon = element.querySelector('i.bi-patch-check');
                if (checkIcon) {
                    checkIcon.classList.remove('bi-patch-check', 'text-muted');
                    checkIcon.classList.add('bi-patch-check-fill', 'text-success');
                }
                alert('Lesson marked as complete! +10 points earned.');
                // We don't necessarily need a full reload if we update UI manually
                // window.location.reload(); 
            }
        } catch (error) {
            console.error('Error marking lesson complete:', error);
        }
    }

    document.getElementById('lowBandwidthToggle')?.addEventListener('change', async (e) => {
        const enabled = e.target.checked;
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        try {
            await fetch('/api/settings/low-bandwidth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify({ enabled })
            });
            window.location.reload();
        } catch (err) {
            console.error('Failed to toggle bandwidth mode', err);
        }
    });

    // Auto-start tracking for the first incomplete lesson
    window.addEventListener('load', () => {
        const firstIncomplete = document.querySelector('.sidebar-item i.bi-play-circle');
        if (firstIncomplete) {
            const lessonId = firstIncomplete.closest('.sidebar-item').id.split('-')[1];
            startProgressTracking(lessonId);
        }
    });
</script>
</body>
</html>
