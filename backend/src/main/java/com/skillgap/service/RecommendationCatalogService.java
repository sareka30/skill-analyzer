package com.skillgap.service;

import com.skillgap.model.Recommendation;
import com.skillgap.model.Skill;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A small, in-memory catalog that maps a missing skill to a suggested
 * learning resource. This keeps the recommendation engine self-contained
 * (no external API calls / API keys required) while still being easy to
 * extend or later back with a real course-provider integration.
 */
@Service
public class RecommendationCatalogService {

    private final Map<String, Recommendation> catalog = new LinkedHashMap<>();

    public RecommendationCatalogService() {
        register("Java", "COURSE", "Java Programming Masterclass", "Udemy",
                "https://www.udemy.com/topic/java/", "Covers core and advanced Java fundamentals.");
        register("Spring Boot", "COURSE", "Spring Boot for Beginners", "Udemy",
                "https://www.udemy.com/topic/spring-boot/", "Hands-on REST API development with Spring Boot.");
        register("React.js", "COURSE", "React - The Complete Guide", "Udemy",
                "https://www.udemy.com/topic/react/", "Build modern single-page apps with React.");
        register("JavaScript", "COURSE", "The Modern JavaScript Bootcamp", "Udemy",
                "https://www.udemy.com/topic/javascript/", "Modern ES6+ JavaScript fundamentals.");
        register("HTML5 & CSS3", "COURSE", "Responsive Web Design", "freeCodeCamp",
                "https://www.freecodecamp.org/learn/2022/responsive-web-design/", "Semantic HTML and modern CSS layout.");
        register("SQL & Databases", "COURSE", "SQL for Data Analysis", "Coursera",
                "https://www.coursera.org/courses?query=sql", "Relational database design and querying.");
        register("Node.js", "COURSE", "Node.js, Express, MongoDB Bootcamp", "Udemy",
                "https://www.udemy.com/topic/nodejs/", "Server-side JavaScript with Node and Express.");
        register("Python", "COURSE", "Python for Everybody", "Coursera",
                "https://www.coursera.org/specializations/python", "Beginner-friendly Python specialization.");
        register("Machine Learning", "CERTIFICATION", "Machine Learning Specialization", "Coursera",
                "https://www.coursera.org/specializations/machine-learning-introduction", "Andrew Ng's ML fundamentals.");
        register("Data Analysis", "COURSE", "Google Data Analytics Certificate", "Coursera",
                "https://www.coursera.org/professional-certificates/google-data-analytics", "End-to-end data analytics workflow.");
        register("Git & GitHub", "PROJECT", "Version Control with Git", "GitHub Learning Lab",
                "https://skills.github.com/", "Practice branching, PRs, and collaboration workflows.");
        register("Docker", "CERTIFICATION", "Docker Certified Associate Prep", "Docker",
                "https://www.docker.com/", "Containerize and ship applications reliably.");
        register("Kubernetes", "CERTIFICATION", "Certified Kubernetes Application Developer", "CNCF",
                "https://www.cncf.io/certification/ckad/", "Deploy and manage containerized workloads.");
        register("AWS Cloud", "CERTIFICATION", "AWS Certified Cloud Practitioner", "AWS Training",
                "https://aws.amazon.com/certification/certified-cloud-practitioner/", "Foundational AWS cloud knowledge.");
        register("CI/CD Pipelines", "CODING_TOPIC", "CI/CD with GitHub Actions", "GitHub Docs",
                "https://docs.github.com/actions", "Automate build, test, and deployment pipelines.");
        register("Communication", "CODING_TOPIC", "Effective Communication for Engineers", "LinkedIn Learning",
                "https://www.linkedin.com/learning/", "Practical workplace and technical communication.");
        register("Problem Solving", "PROJECT", "Data Structures & Algorithms Practice", "LeetCode",
                "https://leetcode.com/", "Sharpen problem-solving with graded coding challenges.");
        register("Team Collaboration", "CODING_TOPIC", "Agile & Scrum Fundamentals", "LinkedIn Learning",
                "https://www.linkedin.com/learning/", "Working effectively in cross-functional teams.");
        register("Time Management", "CODING_TOPIC", "Time Management Fundamentals", "LinkedIn Learning",
                "https://www.linkedin.com/learning/", "Prioritization and productivity techniques.");
    }

    private void register(String skillName, String type, String title, String provider, String url, String description) {
        catalog.put(skillName.toLowerCase(), Recommendation.builder()
                .type(type)
                .title(title)
                .providerOrPlatform(provider)
                .url(url)
                .description(description)
                .build());
    }

    /**
     * Returns a recommendation for the given missing skill, falling back to
     * a generic "search and learn" suggestion for skills not in the catalog.
     */
    public Recommendation recommendationFor(Skill skill) {
        Recommendation template = catalog.get(skill.getName().toLowerCase());
        if (template == null) {
            template = Recommendation.builder()
                    .type("CODING_TOPIC")
                    .title("Learn " + skill.getName())
                    .providerOrPlatform("Self-paced")
                    .url(null)
                    .description("No curated resource yet for this skill — search for a course or official docs on "
                            + skill.getName() + ".")
                    .build();
        }
        return Recommendation.builder()
                .type(template.getType())
                .title(template.getTitle())
                .providerOrPlatform(template.getProviderOrPlatform())
                .url(template.getUrl())
                .description(template.getDescription())
                .build();
    }
}
