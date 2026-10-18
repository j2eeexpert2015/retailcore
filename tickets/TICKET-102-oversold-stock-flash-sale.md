# TICKET-102 · Bug · Oversold stock during flash sale

**Priority:** Critical
**Reported by:** Operations
**Services suspected:** Orders.Api (stock reservation)

## Report

During Friday's flash sale we sold 137 units of a product that had 100 in
stock. The warehouse now has 37 orders it cannot fulfil.

## Steps to reproduce

Reliably reproducible under concurrent load:

1. Seed a product with stock 5 (`./scripts/seed.ps1`)
2. Fire 20 simultaneous `POST http://localhost:5001/orders` requests for
   quantity 1 of that product (a small PowerShell or bash loop with `&` /
   background jobs is enough)
3. Count accepted orders

**Expected:** at most 5 accepted, 15 rejected with an out-of-stock error.
**Actual:** far more than 5 accepted; `stock_items.quantity` goes negative.

## Notes

Single requests behave correctly. Only concurrent requests oversell, which
is why QA never caught it.
