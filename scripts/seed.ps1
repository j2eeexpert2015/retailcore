# DESTRUCTIVE: resets RetailCore PostgreSQL tables to development sample data.
# Prerequisites: docker compose up --build -d (RetailCore + PostgreSQL running).

$ErrorActionPreference = "Stop"

$seedSql = Join-Path $PSScriptRoot "seed.sql"

if (-not (Test-Path $seedSql)) {
    Write-Error "seed.sql not found next to seed.ps1"
    exit 1
}

# Wait for RetailCore: it creates the database schema on startup.
Write-Host "Waiting for RetailCore on http://localhost:8080 ..."
$deadline = (Get-Date).AddSeconds(90)
while ($true) {
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:8080/health" -TimeoutSec 3
        if ($health.status -eq "ok") {
            break
        }
    }
    catch {
        # API not up yet; keep waiting.
    }

    if ((Get-Date) -gt $deadline) {
        Write-Error "RetailCore did not become healthy within 90 seconds. Is docker-compose up?"
        exit 1
    }

    Start-Sleep -Seconds 2
}

Write-Host "RetailCore is up. Applying seed data ..."

Get-Content -Raw $seedSql | docker compose exec -T postgres psql -U retailcore -d retailcore -v ON_ERROR_STOP=1

if ($LASTEXITCODE -ne 0) {
    Write-Error "Seeding failed (psql exit code $LASTEXITCODE)."
    exit $LASTEXITCODE
}

Write-Host "Done. Seeded 7 products, stock levels and 3 orders."
Write-Host "Try:  curl http://localhost:8080/orders/1"
Write-Host "Admin: http://localhost:8080/admin/orders"
