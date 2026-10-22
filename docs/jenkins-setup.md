# Jenkins setup

This is the part that must run on your own Jenkins (it needs your Docker Hub
account, SMTP credentials, and a reachable Jenkins instance). The
`Jenkinsfile` is already in the repo; these steps wire Jenkins up to it.

## 0. Run Jenkins

Easiest is Jenkins in Docker, with the host Docker socket mounted so the
pipeline can build and run containers:

```bash
docker run -d --name jenkins -p 8081:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

The pipeline calls `docker`, `docker compose`, `mvn`, and `curl`. The
`jenkins/jenkins:lts` image does not ship these, so either install them into
the container (exec in as root, install docker-ce-cli, compose plugin, maven,
curl) or — simpler — build a small custom Jenkins image that adds them. Note
that port 8081 is used here so it does not clash with the calculator on 8080.

Unlock with:

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Install suggested plugins, then add: **Docker Pipeline**, **Pipeline Utility
Steps**, and **Email Extension (email-ext)**.

## 1. Docker Hub credentials (never hard-coded)

Manage Jenkins → Credentials → System → Global → Add credentials:
- Kind: Username with password
- Username: `jitin1975`
- Password: a Docker Hub access token (Account Settings → Security → New
  Access Token), not your account password
- ID: `dockerhub`  ← must match `REGISTRY_CREDS` in the Jenkinsfile

## 2. Email notifications

Manage Jenkins → System → Extended E-mail Notification:
- SMTP server: `smtp.gmail.com`, port 465, SSL
- Credentials: your Gmail address + a Gmail **App Password** (not your normal
  password; needs 2FA enabled on the account)
- Default recipient / reply-to: `jitinvenkatasaik2006@gmail.com`

The Jenkinsfile's `post` block emails on both success and failure.

## 3. Create the pipeline job

New Item → **Pipeline** (or Multibranch Pipeline).
- Pipeline → Definition: *Pipeline script from SCM*
- SCM: Git, repo URL = your GitHub repo
- Script Path: `Jenkinsfile`

## 4. Auto-trigger from GitHub

**Webhook (preferred):** GitHub repo → Settings → Webhooks → Add:
- Payload URL: `http://<your-jenkins-host>:8081/github-webhook/`
- Content type: `application/json`
- Event: Just the push event

In the job, tick *GitHub hook trigger for GITScm polling*.

If your Jenkins is on a laptop and not publicly reachable, expose it with
`ngrok http 8081` and use the ngrok URL in the webhook, **or** fall back to
Poll SCM (the problem statement accepts this): in the job, *Build Triggers →
Poll SCM* with schedule `H/2 * * * *`.

## 5. Verify the whole loop

1. Run the job once manually — it should go green through all six stages and
   you should get a success email.
2. Make a trivial code change, commit, and push.
3. Watch the pipeline fire on its own, rebuild, push a new image tag, and
   redeploy. `docker ps` should show `calculator` and `calculator-proxy`
   running, and `curl http://localhost/api/v1/add?a=2&b=3` should answer
   through the proxy.

This push-to-deploy loop is exactly what you demo in Class 17.
