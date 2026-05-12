<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz: ${quiz.title} - RuralEduHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/global.css">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <style>
        :root {
            --primary: #10b981;
            --primary-dark: #059669;
            --secondary: #f59e0b;
            --bg-quiz: #f0fdf4;
        }

        body {
            background: var(--bg-quiz);
            font-family: 'Outfit', sans-serif;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .quiz-container {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
        }

        .quiz-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 32px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
            width: 100%;
            max-width: 800px;
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.5);
            position: relative;
        }

        .quiz-header {
            background: linear-gradient(135deg, #064e3b 0%, #10b981 100%);
            padding: 40px;
            color: white;
            text-align: center;
        }

        .progress-container {
            height: 6px;
            background: rgba(255,255,255,0.2);
            border-radius: 3px;
            margin-top: 20px;
            position: relative;
        }

        .progress-fill {
            height: 100%;
            background: white;
            border-radius: 3px;
            transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .quiz-body {
            padding: 40px;
        }

        .option-btn {
            display: block;
            width: 100%;
            padding: 20px 30px;
            margin-bottom: 16px;
            background: white;
            border: 2px solid #f1f5f9;
            border-radius: 20px;
            text-align: left;
            font-weight: 600;
            color: #475569;
            transition: all 0.2s;
            position: relative;
            cursor: pointer;
        }

        .option-btn:hover {
            border-color: var(--primary);
            background: #f0fdf4;
            transform: scale(1.01);
        }

        .option-btn.selected {
            border-color: var(--primary);
            background: var(--primary);
            color: white;
            box-shadow: 0 10px 20px rgba(16, 185, 129, 0.2);
        }

        .option-btn.correct {
            border-color: #22c55e;
            background: #f0fdf4;
            color: #166534;
        }

        .option-btn.wrong {
            border-color: #ef4444;
            background: #fef2f2;
            color: #991b1b;
        }

        .btn-next {
            background: var(--primary);
            color: white;
            border: none;
            padding: 15px 40px;
            border-radius: 16px;
            font-weight: 700;
            font-size: 1.1rem;
            transition: all 0.2s;
            box-shadow: 0 10px 20px rgba(16, 185, 129, 0.2);
        }

        .btn-next:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
            box-shadow: 0 15px 30px rgba(16, 185, 129, 0.3);
        }

        .btn-next:disabled {
            background: #cbd5e1;
            box-shadow: none;
            cursor: not-allowed;
            transform: none;
        }

        #result-screen {
            display: none;
            text-align: center;
            padding: 60px 40px;
        }

        .score-circle {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            border: 10px solid #f1f5f9;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
            font-weight: 800;
            color: var(--primary);
            margin: 0 auto 30px;
            position: relative;
        }

        .confetti {
            position: absolute;
            pointer-events: none;
            width: 100%;
            height: 100%;
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .animate-question {
            animation: fadeInUp 0.5s ease-out;
        }
    </style>
</head>
<body>

<div class="quiz-container">
    <div class="quiz-card">
        <!-- Progress Bar -->
        <div class="quiz-header">
            <h5 class="text-uppercase small fw-bold opacity-75 mb-2">${course.title}</h5>
            <h2 class="fw-bold mb-0">${quiz.title}</h2>
            <div class="progress-container">
                <div class="progress-fill" id="progress-bar" style="width: 0%"></div>
            </div>
            <div class="mt-3 small opacity-75" id="question-counter">Question 1 of ${quiz.questions.size()}</div>
        </div>

        <div class="quiz-body" id="quiz-screen">
            <div id="question-container" class="animate-question">
                <!-- Question will be injected here -->
            </div>
            
            <div class="d-flex justify-content-between align-items-center mt-5">
                <div id="feedback" class="fw-bold"></div>
                <button class="btn-next" id="next-btn" disabled>Next Question</button>
            </div>
        </div>

        <!-- Result Screen -->
        <div id="result-screen" class="animate-question">
            <div class="score-circle" id="final-score">0%</div>
            <h2 class="fw-bold mb-3" id="result-title">Great Job!</h2>
            <p class="text-muted lead mb-5" id="result-text">You've completed the assessment successfully.</p>
            
            <div class="p-4 rounded-4 bg-light mb-5 d-inline-block px-5">
                <div class="row g-4 text-center">
                    <div class="col-6">
                        <div class="h3 fw-bold mb-0" id="correct-count">0</div>
                        <div class="small text-muted text-uppercase">Correct</div>
                    </div>
                    <div class="col-6">
                        <div class="h3 fw-bold mb-0" id="earned-points">+0</div>
                        <div class="small text-muted text-uppercase">Points</div>
                    </div>
                </div>
            </div>
            
            <div>
                <a href="/student/course/${course.id}" class="btn-next text-decoration-none">Return to Course</a>
            </div>
        </div>
    </div>
</div>

<script>
    const questions = [
        <c:forEach var="q" items="${quiz.questions}" varStatus="status">
        {
            id: ${q.id},
            text: "${q.content.replace('"', '\\"')}",
            options: [
                "${q.optionA.replace('"', '\\"')}",
                "${q.optionB.replace('"', '\\"')}",
                "${q.optionC.replace('"', '\\"')}",
                "${q.optionD.replace('"', '\\"')}"
            ],
            correct: "${q.correctAnswer}"
        }${!status.last ? ',' : ''}
        </c:forEach>
    ];

    let currentQuestionIndex = 0;
    let score = 0;
    let selectedOption = null;

    const questionContainer = document.getElementById('question-container');
    const nextBtn = document.getElementById('next-btn');
    const feedback = document.getElementById('feedback');
    const progressBar = document.getElementById('progress-bar');
    const counter = document.getElementById('question-counter');

    function loadQuestion() {
        const q = questions[currentQuestionIndex];
        selectedOption = null;
        nextBtn.disabled = true;
        nextBtn.innerText = currentQuestionIndex === questions.length - 1 ? 'Finish Quiz' : 'Next Question';
        feedback.innerHTML = '';
        
        counter.innerText = 'Question ' + (currentQuestionIndex + 1) + ' of ' + questions.length;
        progressBar.style.width = ((currentQuestionIndex) / questions.length) * 100 + '%';

        let optionsHtml = '';
        q.options.forEach((opt, i) => {
            const letter = String.fromCharCode(65 + i);
            optionsHtml += '<div class="option-btn" data-val="' + letter + '">' +
                           '<span class="me-3 opacity-50">' + letter + '.</span>' +
                           opt + '</div>';
        });

        questionContainer.innerHTML = '<h4 class="fw-bold mb-5">' + (currentQuestionIndex + 1) + '. ' + q.text + '</h4>' +
                                      '<div class="options-list">' + optionsHtml + '</div>';

        document.querySelectorAll('.option-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                if (selectedOption !== null) return;
                
                selectedOption = btn.dataset.val;
                document.querySelectorAll('.option-btn').forEach(b => b.classList.remove('selected'));
                btn.classList.add('selected');
                
                // Show immediate feedback
                const correct = q.correct;
                if (selectedOption === correct) {
                    btn.classList.add('correct');
                    feedback.innerHTML = '<span class="text-success"><i class="bi bi-check-circle-fill me-2"></i>Correct!</span>';
                    score++;
                } else {
                    btn.classList.add('wrong');
                    document.querySelector(`[data-val="${correct}"]`).classList.add('correct');
                    feedback.innerHTML = '<span class="text-danger"><i class="bi bi-x-circle-fill me-2"></i>Incorrect</span>';
                }
                
                nextBtn.disabled = false;
            });
        });
    }

    nextBtn.addEventListener('click', () => {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            questionContainer.classList.remove('animate-question');
            void questionContainer.offsetWidth; // Trigger reflow
            questionContainer.classList.add('animate-question');
            loadQuestion();
        } else {
            showResults();
        }
    });

    function showResults() {
        progressBar.style.width = '100%';
        document.getElementById('quiz-screen').style.display = 'none';
        const resultScreen = document.getElementById('result-screen');
        resultScreen.style.display = 'block';
        
        const percentage = Math.round((score / questions.length) * 100);
        document.getElementById('final-score').innerText = percentage + '%';
        document.getElementById('correct-count').innerText = score + '/' + questions.length;
        
        const points = percentage >= 80 ? 50 : 0;
        document.getElementById('earned-points').innerText = '+' + points;
        
        if (percentage >= 80) {
            document.getElementById('result-title').innerText = "Excellent Work!";
            document.getElementById('result-text').innerText = "You've mastered this topic and earned 50 points!";
            
            // Submit quiz to backend to mark course as complete
            fetch('/api/courses/${course.id}/quiz/submit', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
                },
                credentials: 'same-origin',
                body: JSON.stringify({ score: percentage })
            })
            .then(response => response.json())
            .then(data => {
                console.log('Quiz submitted:', data);
                if (data.passed) {
                    // Course completed - certificate will be generated automatically
                }
            })
            .catch(error => console.error('Error submitting quiz:', error));
            
            // Update points in backend
            fetch('/api/gamification/add-points', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
                },
                credentials: 'same-origin',
                body: JSON.stringify({ userId: ${user.id}, points: points })
            }).catch(error => console.error('Error adding points:', error));
        } else {
            document.getElementById('result-title').innerText = "Keep Practicing";
            document.getElementById('result-text').innerText = "You need 80% to earn points. Review the course material and try again!";
        }
    }
    
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    loadQuestion();
</script>

</body>
</html>
