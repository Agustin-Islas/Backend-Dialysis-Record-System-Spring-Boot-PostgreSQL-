# Oracle Cloud Always Free deployment

This folder deploys the Spring Boot backend and PostgreSQL on an Oracle Cloud Always Free VM using Docker Compose.

## Recommended layout

- Frontend: Cloudflare Pages
- Backend API: Oracle Cloud VM + Docker Compose
- PostgreSQL: Docker container on the same Oracle VM
- TLS: Caddy with automatic HTTPS
- DNS: Cloudflare
- Backups: daily `pg_dump`

## VM prerequisites

Use an Always Free eligible VM, preferably Ampere A1 with enough memory for Docker, Java and PostgreSQL.

Open these ingress ports in the Oracle Cloud security list or network security group:

- TCP `22` for SSH
- TCP `80` for HTTP
- TCP `443` for HTTPS

Do not expose PostgreSQL publicly.

## Install Docker on Ubuntu

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker "$USER"
```

Log out and back in after adding your user to the `docker` group.

## Deploy

Clone the backend repository on the VM and enter this folder:

```bash
git clone https://github.com/Agustin-Islas/Backend-Dialysis-Record-System-Spring-Boot-PostgreSQL-.git
cd Backend-Dialysis-Record-System-Spring-Boot-PostgreSQL-/deploy/oracle
cp .env.example .env
nano .env
```

Generate a JWT secret:

```bash
openssl rand -base64 64
```

Set `APP_DOMAIN` to the API hostname, for example:

```text
APP_DOMAIN=api.your-domain.com
```

Set `CORS_ALLOWED_ORIGIN_PATTERNS` to the frontend origins that may call the API:

```text
CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:*,http://127.0.0.1:*,https://your-project.pages.dev,https://app.your-domain.com
```

Start the stack:

```bash
docker compose up -d --build
docker compose logs -f backend
```

## DNS

In Cloudflare DNS, create an `A` record:

```text
api.your-domain.com -> ORACLE_VM_PUBLIC_IP
```

Use DNS-only during the first HTTPS certificate issuance if needed, then proxy can be enabled.

## Backups

Run one backup manually:

```bash
set -a
source .env
set +a
./backup-postgres.sh
```

Daily cron example:

```bash
crontab -e
```

```cron
15 3 * * * cd /home/ubuntu/Backend-Dialysis-Record-System-Spring-Boot-PostgreSQL-/deploy/oracle && set -a && . ./.env && set +a && ./backup-postgres.sh >> backup.log 2>&1
```

## Update

```bash
git pull
cd deploy/oracle
docker compose up -d --build
```
