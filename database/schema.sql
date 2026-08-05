-- Database Schema for Skill Gap Analyzer
CREATE DATABASE IF NOT EXISTS skill_gap_analyzer;
USE skill_gap_analyzer;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Resumes Table
CREATE TABLE IF NOT EXISTS resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    extracted_text LONGTEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Job Roles Table
CREATE TABLE IF NOT EXISTS job_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB;

-- 4. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL -- Technical, Soft, Tools, Database, etc.
) ENGINE=InnoDB;

-- 5. Job Role Skills Mapping (Many-to-Many)
CREATE TABLE IF NOT EXISTS job_role_skills (
    job_role_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (job_role_id, skill_id),
    FOREIGN KEY (job_role_id) REFERENCES job_roles(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Student Skills Mapping (Many-to-Many)
CREATE TABLE IF NOT EXISTS student_skills (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Skill Gap Reports Table
CREATE TABLE IF NOT EXISTS skill_gap_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    job_role_id INT NOT NULL,
    resume_id INT NOT NULL,
    match_percentage DOUBLE NOT NULL,
    employability_score DOUBLE NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_role_id) REFERENCES job_roles(id) ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Recommendations Table
CREATE TABLE IF NOT EXISTS recommendations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    type VARCHAR(50) NOT NULL, -- COURSE, CERTIFICATION, PROJECT, CODING_TOPIC
    title VARCHAR(150) NOT NULL,
    provider_or_platform VARCHAR(100) NOT NULL,
    url VARCHAR(255),
    description TEXT,
    FOREIGN KEY (report_id) REFERENCES skill_gap_reports(id) ON DELETE CASCADE
) ENGINE=InnoDB;
