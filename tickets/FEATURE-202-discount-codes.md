# FEATURE-202 · Epic · Discount codes (capstone ticket)

**Priority:** Roadmap commitment
**Services:** Orders.Api, Payments.Api, Notifications.Worker

## Story

Marketing wants percentage and fixed-amount discount codes for campaigns.

## Scope

- New `discount_codes` table: code, type (`percent` | `fixed`), value,
  expires_at, max_redemptions, redemption count
- `POST /orders` accepts an optional `discountCode`; the order total and
  every downstream amount reflect it
- Payments.Api charges the discounted total, never the raw total
- Confirmation notification mentions the applied discount
- Expired, unknown, or exhausted codes are rejected with a clear 400
- Redemptions are counted correctly under concurrent use (remember
  TICKET-102 — do not reintroduce that class of bug)

## Acceptance criteria

- [ ] Unit tests for the discount calculator (percent, fixed, stacking not
      allowed, rounding to cents)
- [ ] Integration test: order with a discount code charges the discounted
      amount through the fake gateway
- [ ] Playwright smoke test: admin page at `/admin/orders` shows the
      discounted total
- [ ] PR with a Claude-generated description, CI green
