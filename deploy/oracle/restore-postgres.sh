#!/usr/bin/env bash
set -euo pipefail

if [ $# -ne 1 ]; then
  echo "Usage: $0 backups/dialysis_db-YYYYMMDD-HHMMSS.dump"
  exit 1
fi

cd "$(dirname "$0")"

backup_file="$1"

docker compose exec -T db pg_restore \
  -U "${POSTGRES_USER}" \
  -d "${POSTGRES_DB}" \
  --clean \
  --if-exists \
  < "${backup_file}"
