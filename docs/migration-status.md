# RetailCore migration and verification status

RetailCore is a Java 21 / Spring Boot 4 monolith migrated from a .NET multi-service ShopFlow teaching starter. It has one Maven project, one application process and a single HTTP port (`8080`). The payment client is an in-process Java call; the scheduled notification poller/worker and in-process queue run in the same JVM. This architecture is **not a byte-for-byte or behaviorally verified port**.

## Verified in previous maintainer-provided local runs

- Nine regular JUnit / H2 / MockMvc tests passed using Maven.
- The Postman collection completed seven API requests with 14 passing assertions against the live app.
- PostgreSQL 16 Testcontainers integration test passed; later rerun showed a clean Hikari shutdown after `@DirtiesContext`.

These results describe previous runs, not execution of this packaged revision. See the root `README.md` for commands, prerequisites and report locations.

## Not yet verified or prepared

- Full PostgreSQL Docker Compose app deployment with seeding and Postman collection.
- Full ShopFlow business behavior parity; delivery date handling and notifications require review.
- Independent reproduction of all six tickets. Some tickets still reference old service ports and .NET terminology; TICKET-101's claimed behavior does not match the current decline handler.

## Purpose of deliberate bugs

The baseline test suite is intended to pass while certain targeted defects remain available for future engineering exercises. Do not silently fix ticket defects during documentation or portability cleanup.
