# GitHub Actions Oracle deploy

This deploys the backend to the Oracle VM on every push to `main`.

The VM keeps the production `.env` file locally. Do not commit real secrets.

## Required GitHub secrets

Create these in the backend repository:

```text
ORACLE_HOST=your.vm.public.ip.or.hostname
ORACLE_PORT=22
ORACLE_USER=ubuntu
ORACLE_SSH_KEY=-----BEGIN OPENSSH PRIVATE KEY-----...
ORACLE_APP_DIR=/opt/dialysis-backend
```

`ORACLE_APP_DIR` must be the folder where this backend repository is cloned on the VM.

## First VM setup

On the VM:

```bash
sudo mkdir -p /opt/dialysis-backend
sudo chown "$USER":"$USER" /opt/dialysis-backend
git clone https://github.com/Agustin-Islas/Backend-Dialysis-Record-System-Spring-Boot-PostgreSQL-.git /opt/dialysis-backend
cd /opt/dialysis-backend/deploy/oracle
cp .env.example .env
nano .env
docker compose up -d --build
```

After the first setup, pushes to `main` run:

```bash
git pull --ff-only origin main
docker compose up -d --build
```
