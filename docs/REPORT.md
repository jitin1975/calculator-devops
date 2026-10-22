# CSE 816 Mini-Project Report — CI/CD Pipeline for a Calculator REST API

**Name:** <your name>  ·  **Roll:** IMT2023108
**Repo:** https://github.com/<you>/calculator-devops  ·  **Image:** https://hub.docker.com/r/jitin1975/calculator-devops

> Convert this to a 2–3 page PDF once filled in. Keep it tight. Paste the
> screenshots where marked.

## 1. Pipeline architecture

```
Developer ──push──> GitHub ──webhook──> Jenkins
                                          │
             Checkout ─ Build ─ Test ─ Docker Build ─ Push ─ Deploy
                                   │              │           │
                             JUnit report    Docker Hub   docker compose up -d
                                                           (calculator + nginx)
                                          │
                                   email on success/failure
```

*(Replace with a cleaner diagram if you like — draw.io or even hand-drawn is fine.)*

## 2. The application

Spring Boot (Spring Web + Actuator), Maven, Java 21. Four GET endpoints under
`/api/v1` returning JSON; a `@RestControllerAdvice` turns bad input and
divide-by-zero into structured HTTP 400 responses. Actuator exposes
`/actuator/health` for the Compose health check. Tested with JUnit 5 (unit
tests per operation incl. divide-by-zero) and a MockMvc integration test.

## 3. Pipeline stages

| Stage | What it does |
|-------|--------------|
| Checkout | Pulls the triggering commit from GitHub |
| Build | `mvn clean package -DskipTests` → `target/calculator.jar` |
| Test | `mvn test`; publishes JUnit XML; build fails on any test failure |
| Docker Build | Multi-stage build, tagged `:<build-number>` and `:latest` |
| Push | Logs in with Jenkins-managed Docker Hub creds, pushes both tags |
| Deploy | `docker compose up -d`, then polls `/actuator/health` until UP |

Email notification fires in the `post` block on both success and failure.

## 4. Containerization & deployment

Multi-stage Dockerfile: a Maven/JDK stage builds the JAR with a cached
dependency layer, a slim `eclipse-temurin:21-jre-alpine` stage runs it as a
non-root user. `docker-compose.yml` brings up the calculator plus an Nginx
reverse proxy (second component), on a user-defined bridge network, with a
health check on `/actuator/health`, a restart policy, and env vars.

## 5. Challenges faced

*(Write your own — real ones read far better than generic ones. Likely
candidates: making the Compose health check work from the alpine image —
curl isn't in the base JRE image, so it's installed in the Dockerfile;
exposing Jenkins to GitHub for the webhook / falling back to Poll SCM;
getting Docker-in-Jenkins to work via the mounted socket; Gmail App Password
for email-ext.)*

## 6. Screenshots

- [ ] Green Jenkins pipeline run (all six stages)
- [ ] The build-status email
- [ ] `docker ps` showing `calculator` and `calculator-proxy` running
- [ ] Sample API responses (add, and the divide-by-zero 400)
