# TICKET-103 · Performance · Order detail endpoint is slow and getting slower

**Priority:** Medium (customer-visible latency)
**Reported by:** Frontend team
**Services suspected:** Orders.Api

## Report

`GET http://localhost:5001/orders/{id}` takes noticeably longer for orders
with many line items. An order with 50 lines takes multiple seconds. The
frontend team has started caching responses client-side to hide it, which
is masking the real problem.

## Steps to reproduce

1. Seed the database (`./scripts/seed.ps1`), then create one order with
   40-50 line items
2. Time the endpoint: `curl -w "%{time_total}" http://localhost:5001/orders/{id}`
3. Watch the SQL statements while the request runs (Postgres logs, or ask
   Claude Code to add temporary EF Core command logging)

**Expected:** one or two queries regardless of line count.
**Actual:** query count grows with the number of line items.
