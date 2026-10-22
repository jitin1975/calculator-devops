#!/usr/bin/env bash
#
# Creates a clean, progressive git history for the calculator-devops project,
# with a feature branch merged back into main, then pushes to GitHub.
#
# BEFORE RUNNING:
#   1. Create an EMPTY repo on GitHub named calculator-devops
#      (no README, no .gitignore, no license — so the push is not rejected).
#   2. Set GH_USER below to your GitHub username.
#   3. Run this from inside the calculator-devops/ folder:  bash setup-git.sh
#
set -euo pipefail

GH_USER="jitin1975"      # <-- edit this
REPO="calculator-devops"

if [ "$GH_USER" = "YOUR_GITHUB_USERNAME" ]; then
    echo "Edit GH_USER at the top of this script first."; exit 1
fi

git init -b main

# 1. skeleton
git add pom.xml .gitignore src/main/resources/application.properties \
        src/main/java/edu/iiitb/calculator/CalculatorApplication.java
git commit -m "chore: initial Spring Boot + Maven skeleton"

# 2. feature branch: the API and its tests
git checkout -b feature/calculator-api

git add src/main/java/edu/iiitb/calculator/CalculatorService.java \
        src/main/java/edu/iiitb/calculator/DivideByZeroException.java
git commit -m "feat: calculator service with the four operations"

git add src/main/java/edu/iiitb/calculator/CalculationResponse.java \
        src/main/java/edu/iiitb/calculator/CalculatorController.java
git commit -m "feat: REST endpoints returning JSON results"

git add src/main/java/edu/iiitb/calculator/ApiError.java \
        src/main/java/edu/iiitb/calculator/GlobalExceptionHandler.java
git commit -m "feat: input validation and structured 400 error responses"

git add src/test
git commit -m "test: JUnit unit tests and MockMvc integration tests"

# 3. merge the feature branch back (no-ff keeps a merge commit in history)
git checkout main
git merge --no-ff feature/calculator-api \
    -m "Merge feature/calculator-api: calculator API with tests"

# 4. DevOps layers, each its own commit
git add Dockerfile .dockerignore
git commit -m "build: multi-stage Dockerfile with non-root runtime"

git add docker-compose.yml nginx/nginx.conf
git commit -m "build: docker compose stack with nginx proxy and health check"

git add Jenkinsfile
git commit -m "ci: declarative Jenkins CI/CD pipeline"

git add README.md docs setup-git.sh
git commit -m "docs: README, Jenkins setup guide, report template"

# 5. push
git remote add origin "https://github.com/${GH_USER}/${REPO}.git"
git push -u origin main
git push origin feature/calculator-api   # so the branch is visible on GitHub

echo
echo "Done. History:"
git --no-pager log --oneline --graph --all

cat <<EOF

Next, if you want an actual Pull Request on GitHub (nicer for the rubric than
a local merge): the feature branch is already pushed, so open
  https://github.com/${GH_USER}/${REPO}/compare/main...feature/calculator-api
create the PR and merge it there. (The local --no-ff merge above already
gives you a merge commit either way, so this is optional.)
EOF
