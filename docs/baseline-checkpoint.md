# Starter baseline checkpoint (Stage 1)

This is the pre-security, pre-external-gateway RetailCore starter. The application code and tests come from `retailcore-test-quality-cleanup.zip`; only the latest concise README and this checkpoint note were applied.

## Application state

- Spring Boot 4.0.1, Java 21, Maven, H2 default; optional PostgreSQL mode.
- Payment simulation is in-process (`PaymentsClient` and `FakePaymentGateway`); there is no external HTTP gateway yet.
- Spring Security / JWT / identity-provider support is not installed yet.
- The six teaching exercises are intentionally **not fixed**.
- Test suites: 7 default JUnit methods, a Postman collection of 7 requests / 14 assertions, and 1 PostgreSQL Testcontainers test under `-Ppostgres-tests`.

## Verify on a developer workstation

Run separately from the `retailcore/` directory:

```bash
mvn clean verify
```

```bash
mvn clean verify -Ppostgres-tests
```

```bash
mvn spring-boot:run
```

Then run the Postman collection against `http://localhost:8080` as described in README.md.

## Verification status

Previous user-reported runs passed before this staged baseline. This packaging step did **not** run tests: Maven and Docker are not available in the packaging environment. Do not label the new archive as independently test-verified until those commands and Postman have been rerun.

## Local Git checkpoint

After checking the baseline on your machine, create a Git repository / commit and tag manually. This distributed ZIP does not contain `.git` history or a precreated tag.

```bash
git init
```

```bash
git add -A
```

```bash
git commit -m "chore: freeze pre-security RetailCore baseline"
```

```bash
git tag retailcore-pre-security-gateway
```

The next phase will introduce a standalone fake payment HTTP service without modifying the six ticket outcomes.
