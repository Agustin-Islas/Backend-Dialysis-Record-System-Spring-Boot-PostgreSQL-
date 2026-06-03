#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

mkdir -p backups

timestamp="$(date +%Y%m%d-%H%M%S)"
backup_file="backups/dialysis_db-${timestamp}.dump"

docker compose exec -T db pg_dump \
  -U "${POSTGRES_USER}" \
  -d "${POSTGRES_DB}" \
  -Fc \
  > "${backup_file}"

find backups -type f -name "dialysis_db-*.dump" -mtime +14 -delete

echo "Backup written to ${backup_file}"
