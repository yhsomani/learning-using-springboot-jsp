package com.ruraledu.controller;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Question;
import com.ruraledu.entity.Quiz;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class QuizController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping("/admin/courses/{courseId}/quiz")
    public ResponseEntity<?> getQuizForCourse(@PathVariable @NonNull Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return ResponseEntity.notFound().build();

        Quiz quiz = course.getQuiz();
        if (quiz == null) {
            return ResponseEntity.status(404).body("No quiz available");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("title", quiz.getTitle());

        List<Map<String, Object>> questionsList = quiz.getQuestions().stream().map(q -> {
            Map<String, Object> qMap = new HashMap<>();
            qMap.put("text", q.getContent());
            List<String> options = new ArrayList<>();
            options.add(q.getOptionA());
            options.add(q.getOptionB());
            options.add(q.getOptionC());
            options.add(q.getOptionD());
            qMap.put("options", options);
            
            int correctIndex = 0;
            if ("B".equals(q.getCorrectAnswer())) correctIndex = 1;
            else if ("C".equals(q.getCorrectAnswer())) correctIndex = 2;
            else if ("D".equals(q.getCorrectAnswer())) correctIndex = 3;
            
            qMap.put("correctOptionIndex", correctIndex);
            return qMap;
        }).collect(Collectors.toList());

        response.put("questions", questionsList);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/courses/{courseId}/quiz")
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> createQuiz(@PathVariable @NonNull Long courseId, @RequestBody Map<String, Object> request) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return ResponseEntity.notFound().build();

        Quiz quiz = course.getQuiz();
        if (quiz == null) {
            quiz = new Quiz();
            quiz.setCourse(course);
        }
        quiz.setTitle((String) request.get("title"));

        List<Map<String, Object>> reqQuestions = (List<Map<String, Object>>) request.get("questions");
        List<Question> questions = new ArrayList<>();
        
        for (Map<String, Object> qReq : reqQuestions) {
            Question q = new Question();
            q.setContent((String) qReq.get("text"));
            
            List<String> options = (List<String>) qReq.get("options");
            if (options != null && options.size() >= 4) {
                q.setOptionA(options.get(0));
                q.setOptionB(options.get(1));
                q.setOptionC(options.get(2));
                q.setOptionD(options.get(3));
            }
            
            int correctIdx = (Integer) qReq.get("correctOptionIndex");
            if (correctIdx == 1) q.setCorrectAnswer("B");
            else if (correctIdx == 2) q.setCorrectAnswer("C");
            else if (correctIdx == 3) q.setCorrectAnswer("D");
            else q.setCorrectAnswer("A");
            
            q.setQuiz(quiz);
            questions.add(q);
        }
        
        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        course.setQuiz(quiz);
        courseRepository.save(course);

        return ResponseEntity.ok(Map.of("message", "Quiz created successfully"));
    }
}
