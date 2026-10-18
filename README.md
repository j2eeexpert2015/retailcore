# RetailCore

RetailCore is a single Maven / Spring Boot monolith for practicing maintenance of a retail order system. It includes orders, inventory, a simulated payment gateway, notifications, an admin page, and H2 or PostgreSQL storage. **Some bugs and incomplete features are deliberately retained for development exercises.**

Run commands from the `retailcore/` directory (where `pom.xml` is located). **H2 is the default; Docker is not needed for ordinary development.**

## 1. Run the application

Start RetailCore with H2:

```bash
mvn spring-boot:run
```

Open these URLs (the full addresses are shown for easy copying):

- Health: [http://localhost:8080/health](http://localhost:8080/health)
- Admin orders: [http://localhost:8080/admin/orders](http://localhost:8080/admin/orders)
- Example order: [http://localhost:8080/orders/1](http://localhost:8080/orders/1)

Press **Ctrl+C** to stop the application. H2 runs in memory, so a fresh restart resets the sample data.

## 2. Run the JUnit tests

```bash
mvn clean verify
```

Runs **7 baseline test methods** without starting the application manually. Docker is not required.

| Test class | Coverage |
| --- | --- |
| `OrderManagerPricingTest` | Currency rounding, including boundary values |
| `H2SeedDataIntegrationTest` | H2 initialization and expected sample records |
| `WebEndpointsIntegrationTest` | Health endpoint, missing-order response, and admin view via MockMvc |
| `OrderWorkflowIntegrationTest` | Successful ordering and stock reservation; declined payment and stock restoration; out-of-stock rejection |

Expected summary: **7 tests, 0 failures, 0 errors**. See `target/surefire-reports/` for results. These tests protect normal functionality; they do **not** prove the intentional ticket defects are fixed.

## 3. Run the Postman API tests

Postman tests call the **running HTTP server** rather than Spring's in-process test framework.

First, start RetailCore in a separate terminal:

```bash
mvn spring-boot:run
```

Then in Postman:

1. Import `postman/RetailCore.postman_collection.json` and `postman/RetailCore.local.postman_environment.json`.
2. Select the **RetailCore Local** environment (`baseUrl` = [http://localhost:8080](http://localhost:8080)).
3. Open **RetailCore API Smoke Tests** → **Run**, select all seven requests, and run one iteration.

The collection contains **7 requests and 14 assertions** covering health, a seeded order, the admin HTML page, order creation, retrieval of the new order, HTTP 404 for a missing order, and the declined-card scenario. The fake gateway can simulate a timeout, so some assertions allow `Pending`; a passing run does not guarantee a decline occurred.

**Tip:** Restart the H2-backed application before repeating the collection, because creating orders modifies its data and stock. For individual manual requests, use `api-requests/retailcore.http` in IntelliJ's HTTP Client.

## 4. Run the PostgreSQL integration test

Start Docker Desktop (or another working Docker daemon), then run:

```bash
mvn clean verify -Ppostgres-tests
```

This runs the **7 JUnit tests plus 1 PostgreSQL Testcontainers test**. `PostgresPersistenceIT` starts a temporary PostgreSQL 16 container, initializes the Spring/JPA schema, saves a product, and retrieves it. The application does not need to be started manually, and Docker Compose is not required.

Expected: **7 baseline tests + 1 PostgreSQL test**, all passing. PostgreSQL results are in `target/failsafe-reports/`. This checks basic PostgreSQL persistence compatibility, **not** full H2/PostgreSQL feature parity.

## 5. Optional: run the full application with PostgreSQL

Build the JAR:

```bash
mvn clean package
```

Start the application and PostgreSQL containers:

```bash
docker compose up --build -d
```

Check their state:

```bash
docker compose ps
```

The application is available at [http://localhost:8080](http://localhost:8080). PostgreSQL mode does not automatically load the H2 sample data. To seed a **disposable** PostgreSQL database, run:

```bash
docker compose exec -T postgres psql -U retailcore -d retailcore -v ON_ERROR_STOP=1 < scripts/seed.sql
```

**Caution:** `scripts/seed.sql` truncates application tables before inserting sample data. Do not run it against data you need to preserve.

Stop the containers:

```bash
docker compose down
```

Database selection for local Maven/IntelliJ runs is controlled by `retailcore.runtime-mode` in `src/main/resources/application.properties` (default: `h2`; optional: `postgres`).

## 6. Why three test suites?

| Suite | Main purpose | When to use |
| --- | --- | --- |
| **JUnit / Spring Boot** | Fast checks of business workflows, MVC behavior, pricing, and H2 seed data | After code changes |
| **Postman** | Checks real HTTP requests, JSON responses, and the admin page on port 8080 | Before demos or after API changes |
| **Testcontainers** | Checks actual PostgreSQL/JPA startup and product persistence | After database changes or before sharing a release |

The suites are complementary. **14 Postman assertions are not 14 JUnit tests.** Deliberately failing ticket-reproduction tests will be kept separate from the passing baseline suite.

## 7. Project structure

| Location | Purpose |
| --- | --- |
| `src/main/java/com/retailcore/orders/` | Order API, pricing, inventory, entities, repositories, and cache logic |
| `src/main/java/com/retailcore/payments/` | Payment API and simulated gateway |
| `src/main/java/com/retailcore/notifications/` | In-process notification polling and processing |
| `src/main/java/com/retailcore/contracts/` | Shared request/response and event types |
| `src/main/resources/` | H2/PostgreSQL settings, H2 sample data, and admin template |
| `src/test/java/com/retailcore/tests/` | JUnit, Spring Boot, and PostgreSQL integration tests |
| `postman/` | Live API smoke-test collection and local environment |
| `api-requests/` | Manual IntelliJ HTTP Client request examples; not automated tests |
| `scripts/` | PostgreSQL seed SQL and helper scripts for disposable local data |
| `tickets/` | Four bug and two feature exercises; descriptions still need reproduction/port review before student use |
| `docs/` | Architecture overview and migration/verification notes |
| `Dockerfile`, `docker-compose.yml` | Optional containerized deployment |

**Note:** The baseline tests previously passed in the maintainer's environment; the test-quality cleanup and this README revision should be verified against your local project before treating the new test count as confirmed.
