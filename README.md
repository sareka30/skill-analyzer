# Skill Gap Analyzer

A full-stack platform where students upload a resume, pick a target job
role, and get a skill-gap report: match %, an employability score, and
curated learning recommendations for every missing skill. Admins manage
the skill and job-role catalog.

- **Backend:** Spring Boot 3.3 (Java 21), Spring Security + JWT, Spring
  Data JPA, MySQL, PDFBox / Apache POI for resume parsing.
- **Frontend:** React 18 + Vite, React Router, Axios.
- **Database:** MySQL 8.

## Project layout

```
SkillGapAnalyzer/
├── backend/     Spring Boot API (Maven project)
├── frontend/    React + Vite single-page app
├── database/    Reference schema.sql / data.sql (optional — see note below)
└── README.md
```

> **Note on the database scripts:** `database/schema.sql` and
> `database/data.sql` are kept as a reference for a manual/raw-SQL setup.
> In normal use you **do not need to run them yourself** — the backend
> creates the schema automatically (`spring.jpa.hibernate.ddl-auto=update`)
> and seeds the reference skills/job roles/admin account itself on first
> startup (`config/DataInitializer.java`), idempotently.

## Prerequisites

| Tool   | Version  |
|--------|----------|
| Java   | 21       |
| Maven  | 3.9+     |
| Node.js| 18+ (20+ recommended) |
| npm    | 9+       |
| MySQL  | 8.x, running locally (or reachable) |

## 1. Database

Create the database (the app can also auto-create it via
`createDatabaseIfNotExist=true` in the JDBC URL, but you still need a
running MySQL server and a user that can connect):

```sql
CREATE DATABASE IF NOT EXISTS skill_gap_analyzer;
```

No further manual steps needed — tables and seed data are created
automatically on first backend startup.

## 2. Backend

```bash
cd backend

# Optional: override defaults instead of editing application.properties
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=change-this-to-a-long-random-string
export CORS_ALLOWED_ORIGINS=http://localhost:5173

mvn clean install
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.
Swagger UI: http://localhost:8080/swagger-ui.html

A default admin account is seeded automatically:
- username: `admin`
- password: `admin123`

**Change or remove this account before deploying anywhere public.**

## 3. Frontend

```bash
cd frontend
cp .env.example .env   # adjust VITE_API_BASE_URL if your backend isn't on :8080
npm install
npm run dev
```

The app starts on **http://localhost:5173** (Vite's dev server also
proxies `/api` to `http://localhost:8080`, so the `.env` step is optional
for local development — it matters once you deploy frontend and backend
on different hosts).

## Environment variables (backend)

| Variable               | Default                                            | Purpose                        |
|-------------------------|----------------------------------------------------|---------------------------------|
| `SERVER_PORT`           | `8080`                                              | API port                        |
| `DB_URL`                | `jdbc:mysql://localhost:3306/skill_gap_analyzer...` | JDBC URL                        |
| `DB_USERNAME`           | `root`                                              | MySQL username                  |
| `DB_PASSWORD`           | `root`                                              | MySQL password                  |
| `DDL_AUTO`               | `update`                                            | Hibernate schema strategy       |
| `UPLOAD_DIR`            | `uploads/resumes`                                   | Where uploaded resumes are saved|
| `JWT_SECRET`            | (dev default — **override in production**)          | HMAC signing key for JWT        |
| `JWT_EXPIRATION_MS`     | `86400000` (24h)                                    | Token lifetime                  |
| `CORS_ALLOWED_ORIGINS`  | `http://localhost:5173`                             | Comma-separated allowed origins |

## API overview

| Method | Path                          | Access          | Purpose                       |
|--------|-------------------------------|-----------------|--------------------------------|
| POST   | `/api/auth/register`          | public          | Create a STUDENT account       |
| POST   | `/api/auth/login`             | public          | Get a JWT                      |
| GET    | `/api/skills`                 | authenticated   | List all skills                |
| POST/PUT/DELETE | `/api/admin/skills(/{id})` | ADMIN      | Manage skills                  |
| GET    | `/api/roles`, `/api/roles/{id}` | authenticated | List / view job roles        |
| POST/PUT/DELETE | `/api/admin/roles(/{id})`  | ADMIN      | Manage job roles               |
| GET/PUT| `/api/student/me`, `/api/student/skills` | STUDENT/ADMIN | View profile / set your skills |
| POST   | `/api/student/resumes/upload` | STUDENT/ADMIN   | Upload + parse a resume (PDF/DOCX) |
| GET    | `/api/student/resumes`        | STUDENT/ADMIN   | List your resumes              |
| POST   | `/api/student/reports/generate?resumeId=&jobRoleId=` | STUDENT/ADMIN | Generate a skill-gap report |
| GET    | `/api/student/reports`, `/api/student/reports/{id}` | STUDENT/ADMIN | List / view your reports |

## Known limitations / things to harden before production

- `spring.jpa.hibernate.ddl-auto=update` is convenient for local dev but
  not safe for production schema management — switch to `validate` and
  manage migrations with Flyway/Liquibase, using `database/schema.sql`
  as your baseline.
- The JWT secret and default admin password **must** be changed via
  environment variables before any real deployment.
- Uploaded resumes are stored on local disk (`UPLOAD_DIR`) — for a
  multi-instance deployment, use shared/object storage (e.g. S3) instead.
- There's no rate limiting on `/api/auth/**` — add one (e.g. via a
  gateway or Bucket4j) before exposing this publicly.
