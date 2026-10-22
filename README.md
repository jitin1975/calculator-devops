# calculator-devops

A small Spring Boot Calculator REST API wrapped in a full CI/CD pipeline:
GitHub → Jenkins → Docker → Docker Compose. Built for CSE 816 (Software
Production Engineering).

The application is deliberately simple; the point is the DevOps engineering
around it. A push to GitHub triggers Jenkins to build, test, package,
containerize, push the image to Docker Hub, and redeploy the stack — no
manual steps.

## API

Base path: `/api/v1`. All operations take two numeric query params `a` and `b`.

| Operation | Endpoint | Example |
|-----------|----------|---------|
| Add | `GET /api/v1/add?a=&b=` | `/api/v1/add?a=2&b=3` → `5` |
| Subtract | `GET /api/v1/subtract?a=&b=` | `/api/v1/subtract?a=5&b=2` → `3` |
| Multiply | `GET /api/v1/multiply?a=&b=` | `/api/v1/multiply?a=4&b=3` → `12` |
| Divide | `GET /api/v1/divide?a=&b=` | `/api/v1/divide?a=6&b=3` → `2` |
| Health | `GET /actuator/health` | Spring Actuator probe (UP/DOWN) |

Success response:

```json
{ "operation": "add", "a": 2, "b": 3, "result": 5 }
```

Errors (missing param, non-numeric param, divide by zero) return HTTP 400 with:

```json
{ "timestamp": "...", "status": 400, "error": "division by zero is not allowed", "path": "/api/v1/divide" }
```

## Build and test

```bash
mvn clean package      # builds target/calculator.jar
mvn test               # runs unit + integration tests, writes surefire XML
```

## Run locally (without Docker)

```bash
mvn spring-boot:run
curl "http://localhost:8080/api/v1/add?a=2&b=3"
curl "http://localhost:8080/actuator/health"
```

## Run with Docker

```bash
docker build -t jitin1975/calculator-devops:latest .
docker run -p 8080:8080 jitin1975/calculator-devops:latest
```

## Run the full stack (Compose)

Brings up the API plus an Nginx reverse proxy on port 80, with a health
check on `/actuator/health`.

```bash
docker compose up -d
curl "http://localhost/api/v1/multiply?a=4&b=3"   # through the proxy
docker compose ps
docker compose down
```

## CI/CD (Jenkins)

The pipeline is defined in `Jenkinsfile` (declarative). Stages: Checkout →
Build → Test (publishes JUnit results) → Docker Build → Push (Docker Hub,
via Jenkins credentials) → Deploy (`docker compose up -d` + health verify).
Build status is emailed on success and failure.

Jenkins setup notes are in `docs/jenkins-setup.md`.

## Layout

```
calculator-devops/
├── src/main/java/edu/iiitb/calculator/   # application
├── src/test/java/edu/iiitb/calculator/   # JUnit 5 + MockMvc tests
├── pom.xml
├── Dockerfile                            # multi-stage build
├── .dockerignore
├── docker-compose.yml                    # app + nginx proxy
├── nginx/nginx.conf
├── Jenkinsfile                           # declarative CI/CD pipeline
├── .gitignore
└── README.md
```
