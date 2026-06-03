#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-$(cd "$(dirname "$0")/../.." && pwd)}"

cd "$APP_DIR"

git pull --ff-only origin main

cd deploy/oracle

docker compose up -d --build
docker compose ps
docker image prune -f
