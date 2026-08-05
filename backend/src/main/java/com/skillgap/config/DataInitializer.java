package com.skillgap.config;

import com.skillgap.model.JobRole;
import com.skillgap.model.Role;
import com.skillgap.model.Skill;
import com.skillgap.model.User;
import com.skillgap.repository.JobRoleRepository;
import com.skillgap.repository.SkillRepository;
import com.skillgap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Seeds the database with the same reference skills / job roles / admin
 * account that used to live only in database/data.sql. Runs on every
 * startup but is idempotent (it only inserts what's missing), so it works
 * safely with spring.jpa.hibernate.ddl-auto=update.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SkillRepository skillRepository;
    private final JobRoleRepository jobRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Map<String, String> skillsToSeed = new LinkedHashMap<>();
        skillsToSeed.put("Java", "Technical");
        skillsToSeed.put("Spring Boot", "Technical");
        skillsToSeed.put("React.js", "Technical");
        skillsToSeed.put("JavaScript", "Technical");
        skillsToSeed.put("HTML5 & CSS3", "Technical");
        skillsToSeed.put("SQL & Databases", "Technical");
        skillsToSeed.put("Node.js", "Technical");
        skillsToSeed.put("Python", "Technical");
        skillsToSeed.put("Machine Learning", "Technical");
        skillsToSeed.put("Data Analysis", "Technical");
        skillsToSeed.put("Git & GitHub", "Tools");
        skillsToSeed.put("Docker", "Tools");
        skillsToSeed.put("Kubernetes", "Tools");
        skillsToSeed.put("AWS Cloud", "Tools");
        skillsToSeed.put("CI/CD Pipelines", "Tools");
        skillsToSeed.put("Communication", "Soft");
        skillsToSeed.put("Problem Solving", "Soft");
        skillsToSeed.put("Team Collaboration", "Soft");
        skillsToSeed.put("Time Management", "Soft");

        skillsToSeed.forEach((name, category) -> {
            if (skillRepository.findByName(name).isEmpty()) {
                skillRepository.save(Skill.builder().name(name).category(category).build());
            }
        });

        seedRole("Full Stack Developer",
                "Responsible for building both the frontend (user-facing) and backend (server-side) of web applications.",
                "Java", "Spring Boot", "React.js", "JavaScript", "HTML5 & CSS3", "SQL & Databases",
                "Git & GitHub", "Communication", "Problem Solving");

        seedRole("Data Scientist",
                "Specializes in analyzing, processing, and modeling data to build predictive models and derive business insights.",
                "SQL & Databases", "Python", "Machine Learning", "Data Analysis", "Git & GitHub",
                "Communication", "Problem Solving");

        seedRole("Frontend Engineer",
                "Focuses on building the user interface and optimizing client-side performance of web applications.",
                "React.js", "JavaScript", "HTML5 & CSS3", "Git & GitHub", "Communication", "Problem Solving",
                "Team Collaboration");

        seedRole("DevOps Engineer",
                "Automates software development pipelines, manages cloud infrastructure, and monitors production environments.",
                "Git & GitHub", "Docker", "Kubernetes", "AWS Cloud", "CI/CD Pipelines", "Problem Solving",
                "Team Collaboration");

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@skillgap.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }

    private void seedRole(String title, String description, String... skillNames) {
        if (jobRoleRepository.findByTitle(title).isPresent()) {
            return;
        }
        Set<Skill> required = new LinkedHashSet<>();
        for (String name : skillNames) {
            skillRepository.findByName(name).ifPresent(required::add);
        }
        jobRoleRepository.save(JobRole.builder()
                .title(title)
                .description(description)
                .requiredSkills(required)
                .build());
    }
}
