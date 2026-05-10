with open('src/main/java/com/ruraledu/controller/AdminController.java', 'r') as f:
    content = f.read()

target = """        for (User u : userRepository.findAll()) {
            writer.println(String.format("%d,%s,%s,%s,%d,%s",
                u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getPoints(), u.isEnabled() ? "Active" : "Disabled"));
        }
    }
}"""

replacement = """        for (User u : userRepository.findAll()) {
            writer.println(String.format("%d,%s,%s,%s,%d,%s",
                u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getPoints(), u.isEnabled() ? "Active" : "Disabled"));
        }
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses";
    }

    @GetMapping("/certificates")
    public String certificates(Model model) {
        return "admin/certificates";
    }
}"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("WARNING: target not found in AdminController.java")

with open('src/main/java/com/ruraledu/controller/AdminController.java', 'w') as f:
    f.write(content)
