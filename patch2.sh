# 2. Course Validation
sed -i '31a\
        if (lessons == null || lessons.isEmpty()) {\n            throw new IllegalArgumentException("Course must have at least one lesson.");\n        }\n' ./src/main/java/com/ruraledu/service/CourseService.java

sed -i '/final Course savedCourse = courseService.saveCourseWithLessons(course, lessons);/i\
        if (lessons.isEmpty()) {\n            return ResponseEntity.badRequest().body(Map.of("message", "Course must have at least one valid video to be imported."));\n        }' ./src/main/java/com/ruraledu/controller/AdminCourseController.java
