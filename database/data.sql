-- Seed Data for Skill Gap Analyzer
USE skill_gap_analyzer;

-- 1. Insert Job Roles
INSERT INTO job_roles (id, title, description) VALUES
(1, 'Full Stack Developer', 'Responsible for building both the frontend (user-facing) and backend (server-side) of web applications.'),
(2, 'Data Scientist', 'Specializes in analyzing, processing, and modeling data to build predictive models and derive business insights.'),
(3, 'Frontend Engineer', 'Focuses on building the user interface and optimizing client-side performance of web applications.'),
(4, 'DevOps Engineer', 'Automates software development pipelines, manages cloud infrastructure, and monitors production environments.');

-- 2. Insert Skills
INSERT INTO skills (id, name, category) VALUES
-- Technical Skills (Web & Backend)
(1, 'Java', 'Technical'),
(2, 'Spring Boot', 'Technical'),
(3, 'React.js', 'Technical'),
(4, 'JavaScript', 'Technical'),
(5, 'HTML5 & CSS3', 'Technical'),
(6, 'SQL & Databases', 'Technical'),
(7, 'Node.js', 'Technical'),
-- Data & ML
(8, 'Python', 'Technical'),
(9, 'Machine Learning', 'Technical'),
(10, 'Data Analysis', 'Technical'),
-- Tools & Cloud
(11, 'Git & GitHub', 'Tools'),
(12, 'Docker', 'Tools'),
(13, 'Kubernetes', 'Tools'),
(14, 'AWS Cloud', 'Tools'),
(15, 'CI/CD Pipelines', 'Tools'),
-- Soft Skills
(16, 'Communication', 'Soft'),
(17, 'Problem Solving', 'Soft'),
(18, 'Team Collaboration', 'Soft'),
(19, 'Time Management', 'Soft');

-- 3. Map Skills to Job Roles (job_role_skills)
-- Full Stack Developer (Java, Spring Boot, React.js, JavaScript, HTML5/CSS3, SQL, Git, Problem Solving, Communication)
INSERT INTO job_role_skills (job_role_id, skill_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 11), (1, 16), (1, 17);

-- Data Scientist (Python, Machine Learning, Data Analysis, SQL, Git, Problem Solving, Communication)
INSERT INTO job_role_skills (job_role_id, skill_id) VALUES
(2, 6), (2, 8), (2, 9), (2, 10), (2, 11), (2, 16), (2, 17);

-- Frontend Engineer (React.js, JavaScript, HTML5/CSS3, Git, Team Collaboration, Communication, Problem Solving)
INSERT INTO job_role_skills (job_role_id, skill_id) VALUES
(3, 3), (3, 4), (3, 5), (3, 11), (3, 16), (3, 17), (3, 18);

-- DevOps Engineer (Docker, Kubernetes, AWS Cloud, CI/CD Pipelines, Git, Problem Solving, Team Collaboration)
INSERT INTO job_role_skills (job_role_id, skill_id) VALUES
(4, 11), (4, 12), (4, 13), (4, 14), (4, 15), (4, 17), (4, 18);

-- 4. Default Admin User
-- Password is 'admin123' hashed with BCrypt
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@skillgap.com', '$2a$10$R/dVma7Xg0.kX0q5eY5q7.J.hN4.N5i56Sg4zR7R2S5d7R9V2U9Wq', 'ADMIN');
