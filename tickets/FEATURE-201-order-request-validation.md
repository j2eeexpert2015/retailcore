# FEATURE-201 · Feature · Validate order creation requests

**Priority:** High (currently a data-quality and security hole)
**Services:** Orders.Api

## Story

As the operations team, we need `POST /orders` to reject bad requests so
that garbage orders stop reaching the warehouse.

Today the endpoint accepts any payload: unknown product ids, zero and
negative quantities, empty orders, and absurd quantities all create rows.

## Acceptance criteria

- [ ] Reject an order with no lines (400, problem-details body)
- [ ] Reject quantity <= 0 and quantity > 100 per line (400)
- [ ] Reject unknown product ids (400, listing the offending ids)
- [ ] Valid orders behave exactly as before (regression-free)
- [ ] Validation failures return RFC 7807 problem details with a stable
      `errors` map the frontend can render
- [ ] Unit tests cover every rejection rule and one happy path
