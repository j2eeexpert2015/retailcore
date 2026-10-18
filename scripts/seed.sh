#!/usr/bin/env bash
# DESTRUCTIVE: resets RetailCore PostgreSQL tables to development sample data.
# Prerequisites: docker compose up --build -d (RetailCore + PostgreSQL running).
# Mac/Linux equivalent of seed.ps1.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SEED_SQL="$SCRIPT_DIR/seed.sql"

if [ ! -f "$SEED_SQL" ]; then
  echo "seed.sql not found next to seed.sh" >&2
  exit 1
fi

# Wait for RetailCore: it creates the database schema on startup.
echo "Waiting for RetailCore on http://localhost:8080 ..."
deadline=$(( $(date +%s) + 90 ))
until curl -sf http://localhost:8080/health > /dev/null 2>&1; do
  if [ "$(date +%s)" -gt "$deadline" ]; then
    echo "RetailCore did not become healthy within 90 seconds. Is docker-compose up?" >&2
    exit 1
  fi
  sleep 2
done

echo "RetailCore is up. Applying seed data ..."
docker compose exec -T postgres psql -U retailcore -d retailcore -v ON_ERROR_STOP=1 < "$SEED_SQL"

echo "Done. Seeded 7 products, stock levels and 3 orders."
echo "Try:  curl http://localhost:8080/orders/1"
echo "Admin: http://localhost:8080/admin/orders"
