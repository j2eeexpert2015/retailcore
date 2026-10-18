# TICKET-101 · Bug · Order shows "Paid" even when the card was declined

**Priority:** High
**Reported by:** Customer support (3 duplicate reports this week)
**Services suspected:** Orders.Api, Payments.Api

## Report

A customer paid with a test card that the gateway declines. The gateway
correctly returned a decline, but the order in RetailCore still moved to
status `Paid`, and the customer received a confirmation email from the
notifications worker.

## Steps to reproduce

1. `docker-compose up -d` and seed the database: `./scripts/seed.ps1`
2. Create an order via `POST http://localhost:5001/orders` (any seeded product)
3. Pay for it with the always-declines test card number `4000 0000 0000 0002`
   via `POST http://localhost:5002/payments/charge`
4. Fetch the order: `GET http://localhost:5001/orders/{id}`

**Expected:** order status `PaymentFailed`, no confirmation email.
**Actual:** order status `Paid`, confirmation email sent.

## Notes from support

There is nothing in the logs. Not a single error line, which is itself
suspicious given the gateway definitely declined.
