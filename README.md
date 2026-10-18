# RetailCore — Java 21 / Spring Boot 4 Starter

RetailCore is a **single-process Spring Boot 4.0.1 monolith** and Maven project for practicing maintenance of an unfamiliar retail order system. It has orders, inventory, an in-process fake payment gateway, simulated notifications, a small Thymeleaf admin view, and either H2 or PostgreSQL persistence. **Some bugs and incomplete features are intentionally retained** for engineering exercises; a green baseline suite does not mean the application is defect-free.

## 1. What you need

| Task | Prerequisites |
| --- | --- |
| H2 development and seven baseline JUnit tests | JDK 21, Maven 3.9+ |
| API tests with Postman | Same prerequisites, plus Postman desktop app; application must be running |
| PostgreSQL integration test | JDK 21, Maven 3.9+, running Docker Desktop/daemon |
| Full PostgreSQL deployment | Docker Compose plus JDK 21/Maven to build the JAR |

Check prerequisites:

```bash
java -version
mvn -version
```

Run all commands below **from the `retailcore/` directory**, which contains `pom.xml`. On Windows, PowerShell, Command Prompt, and Git Bash can run the Maven commands. The default configuration runs with **H2 and no Docker**.

## 2. Quick start — H2, no Docker

```bash
mvn clean verify
mvn spring-boot:run
```

The first command builds the application and runs seven baseline tests without starting a separate web server. The second starts **one** Spring Boot application on port 8080. Keep that terminal open; use a second terminal or Postman for HTTP requests. H2 uses an in-memory database: the schema and sample data are initialized again on each fresh application startup.

Open:

- [Health](http://localhost:8080/health) — expects service `retailcore` with status `ok`.
- [Admin orders](http://localhost:8080/admin/orders) — HTML table of recent orders.
- [Example order](http://localhost:8080/orders/1) — JSON for seeded order 1.

To stop the app, press **Ctrl+C**. If port 8080 is busy, stop the other application using it. The REST APIs share the same port: `GET /health`, `GET /orders/{id}`, `POST /orders`, `POST /payments/charge`, and `GET /admin/orders`.

## 3. Run all seven baseline JUnit tests

**No Docker, database installation, or manually started application is needed.**

```bash
mvn clean verify
```

Maven Surefire discovers and runs these tests against H2 and the Spring test context:

| Test class | Number | What it checks |
| --- | ---: | --- |
| `ContractsTests` | 1 | API contract/DTO behavior |
| `H2StartupSmokeTests` | 1 | Spring context and seeded H2 database |
| `HttpAndAdminTests` | 2 | HTTP routing/admin rendering using MockMvc |
| `OrderWorkflowIntegrationTests` | 3 | Order workflow with deterministic mocked payment interaction |
| `PricingTests` | 2 | Selected pricing calculations |
| **Total** | **9** | **Baseline tests, not every known bug** |

Look for a total of `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`. Reports live in `target/surefire-reports/`. The integration test named `PostgresPersistenceIT` is **not** included in this default command.

## 4. Run the Postman API collection against a live server

**Postman cannot run the collection unless RetailCore is started and reachable.**

1. Start RetailCore in Terminal A: `mvn spring-boot:run` (default H2).
2. In Postman, import `postman/RetailCore.postman_collection.json` and `postman/RetailCore.local.postman_environment.json`.
3. Select the environment **RetailCore Local**, which defines `baseUrl` as `http://localhost:8080`.
4. Select the **RetailCore API Smoke Tests** collection, click **Run**, leave its **7 requests** enabled, and set **1 iteration**.
5. Run the complete collection and inspect both request statuses and assertion results. A previous local run produced **14 passing assertions**; counts may change if the collection is edited.

The collection checks health, a seeded order, the admin page, order creation, chained retrieval, a missing order, and a declined-card scenario. **It changes application data and inventory**; use a freshly restarted H2 app for repeatable results. The fake gateway can simulate timeouts, and relevant assertions permit a `Pending` outcome, so passing the collection is *not* proof that every payment bug is absent.

To run individual requests from IntelliJ IDEA's HTTP Client, open `api-requests/retailcore.http`. The file is a **manual request scratchpad**, not a second automated test suite. Some JetBrains HTTP Client features depend on IntelliJ edition/version.

## 5. Run PostgreSQL integration testing with Testcontainers

Start **Docker Desktop** (or another supported Docker daemon) first, then run:

```bash
mvn clean verify -Ppostgres-tests
```

This runs **seven baseline JUnit tests plus `PostgresPersistenceIT` (one test)**, using Maven Failsafe. Testcontainers creates a temporary PostgreSQL 16 container automatically. You **do not** manually start RetailCore or `docker compose` for this test. The first execution may take longer while images download. Look for `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` under Failsafe and `BUILD SUCCESS`; its reports are in `target/failsafe-reports/`.

The test verifies schema creation and a repository save/retrieve round trip. It is **not a full PostgreSQL functional-parity suite**. The Failsafe JVM is configured to use UTC to avoid JDBC timezone alias problems on some Windows configurations. `PostgresPersistenceIT` marks its Spring context dirty after the class so Hikari can close before the temporary database container is stopped. Mockito may still show Java-agent warnings; those are separate from database tests.

If you see `Could not find a valid Docker environment`, ensure Docker Desktop is running and available to the same user account. If you see `Asia/Calcutta` in a PostgreSQL timezone rejection, verify that the included Failsafe UTC configuration is present in `pom.xml`.

## 6. Run a full PostgreSQL application with Docker Compose (optional)

This is **different** from Testcontainers. It runs the actual application container and a persistent PostgreSQL service:

```bash
mvn clean package
docker compose up --build -d
docker compose ps
```

Compose exposes the app at `http://localhost:8080` and PostgreSQL at port 5432. The PostgreSQL profile does not automatically load sample rows; to initialize a **fresh development database** after the app creates its schema, run:

```bash
docker compose exec -T postgres psql -U retailcore -d retailcore -v ON_ERROR_STOP=1 < scripts/seed.sql
```

**Warning:** `scripts/seed.sql` truncates the existing application tables before inserting sample data. Use it only on disposable development databases; never on production or data you need to keep. You can instead call the `scripts/seed.ps1` PowerShell or `scripts/seed.sh` Bash helpers after containers are running. Both target the one-app architecture on port 8080.

```bash
docker compose down
```

To remove the PostgreSQL volume and **permanently erase that data**, run `docker compose down -v`.

Alternatively run **only PostgreSQL** with `docker compose up -d postgres`, change the single property `retailcore.runtime-mode=postgres` in `src/main/resources/application.properties`, and run the app with Maven or IntelliJ. Set it back to `h2` when finished. The default property is `retailcore.runtime-mode=${RETAILCORE_RUNTIME_MODE:h2}`; the environment variable `RETAILCORE_RUNTIME_MODE=postgres` is another supported override.

## 7. Testing strategy: why three suites exist

RetailCore uses **three complementary forms of verification**, each with a deliberately limited responsibility. Their numbers are not interchangeable: JUnit has **seven test methods** in the default build, the Postman collection has **seven HTTP requests with 14 assertions**, and PostgreSQL testing adds **one Testcontainers test method**. Neither the passing baseline nor Postman claims to expose the intentional teaching-ticket defects.

| Suite | Purpose | When to run | What it does **not** establish |
| --- | --- | --- | --- |
| JUnit + Spring Boot + H2 | Fast business logic, controller contract and repository/seed checks | Every code change: `mvn clean verify` | Real network deployment, PostgreSQL compatibility, or concurrency safety |
| Postman | Live HTTP/HTML smoke test using the running application on port 8080 | Before demos and when changing API endpoints | Detailed internal logic, database-specific behavior, or exhaustive gateway scenarios |
| Testcontainers PostgreSQL | Independent verification that real PostgreSQL 16 works with Spring Boot/JPA | Before a release, or after entity/schema changes: `mvn clean verify -Ppostgres-tests` | Full PostgreSQL/H2 feature parity or real deployment through Docker Compose |

### Exact coverage of the current JUnit tests (7 methods)

| Java test class | Test behavior | Why it is useful |
| --- | --- | --- |
| `OrderManagerPricingTest` | Rounds currency to two places using HALF_UP, including half-cent and zero cases | Protects monetary calculation precision |
| `H2SeedDataIntegrationTest` | Loads three orders and seven products; verifies SKU-1005 is absent | Catches broken local startup SQL and sample data |
| `WebEndpointsIntegrationTest` | Verifies `/health` and HTTP 404 for an unknown order; verifies admin view/model | Guards Spring MVC controller responses without starting a separate web server |
| `OrderWorkflowIntegrationTest` | Confirms approved order, normalized customer email, reserved stock and retrievable details | Protects the main happy-path workflow |
| `OrderWorkflowIntegrationTest` | Declines payment and restores stock | Protects the explicit decline branch, not simulated timeouts |
| `OrderWorkflowIntegrationTest` | Rejects an out-of-stock order without persisting it | Protects an essential inventory failure path |

`WebEndpointsIntegrationTest` uses standalone **MockMvc**, not a socket connection to the running app. `OrderWorkflowIntegrationTest` uses real H2-backed repositories but a mocked payment client for deterministic outcomes. Two low-value checks were removed during cleanup: a Java-record accessor test and a reference-prefix-only assertion. The original implementation and intended teaching defects were not altered.

### Exact coverage of the Postman collection (7 requests, 14 assertions)

| Request | Assertions |
| --- | --- |
| Health | HTTP 200; JSON `service` is `retailcore` |
| Seeded order | HTTP 200; seeded order ID equals 1 |
| Admin page | HTTP 200; HTML includes `Recent Orders` |
| Create order | HTTP 201; numeric order ID; status is Confirmed **or** Pending |
| Retrieve created order | HTTP 200; returned ID matches the previous request's stored ID |
| Missing order | HTTP 404 |
| Declined-card scenario | HTTP 201; status is Declined **or** Pending |

`Pending` is accepted in two Postman scenarios because the fake payment gateway can simulate timeouts. Thus **14 passing assertions do not prove a decline occurred**. The collection creates orders and changes stock; start from a fresh H2 process for repeatability.

### Exact coverage of the PostgreSQL integration test (1 method)

`PostgresPersistenceIT` uses Testcontainers to start **PostgreSQL 16**, boots the Spring application with the postgres profile, lets Hibernate initialize the schema, saves a product via `ProductRepository`, then retrieves it by ID and verifies its SKU. It tests **basic schema/repository compatibility**, not complete database parity. `@DirtiesContext` closes the Spring context before the temporary container shuts down; the Failsafe test JVM uses UTC.

### Test naming and scope

JUnit method names describe the expected behavior, and each test has a one-line comment explaining the business rule or failure it protects. Intentionally failing ticket-reproduction tests belong in a separately invoked exercise suite, **not** in the ordinary green baseline. The current test count is **7 default JUnit methods + 1 optional PostgreSQL method**. Earlier run totals refer to the previous version and are not fresh verification of this edited package.

## 8. Project layout and why each folder exists

```text
retailcore/
├── pom.xml                         # Single Maven build; postgres-tests profile
├── Dockerfile                      # Packages the executable Spring Boot JAR
├── docker-compose.yml              # Optional app + PostgreSQL deployment
├── src/
│   ├── main/
│   │   ├── java/com/retailcore/
│   │   │   ├── RetailCoreApplication.java   # One Spring Boot entry point
│   │   │   ├── orders/              # REST controllers, entities, JPA repositories,
│   │   │   │                        # pricing/orders, inventory and cache logic
│   │   │   ├── payments/            # Payment controller/client, fake gateway
│   │   │   ├── notifications/       # Scheduled poller, consumer, in-process queue use
│   │   │   └── contracts/           # Shared request/response DTOs and event types
│   │   └── resources/
│   │       ├── application.properties          # Select h2 or postgres
│   │       ├── application-h2.properties       # H2 config and SQL initialization
│   │       ├── application-postgres.properties # PostgreSQL config
│   │       ├── data-h2.sql                     # Automatic H2 sample records
│   │       └── templates/admin/orders.html     # Thymeleaf page
│   └── test/java/com/retailcore/tests/         # JUnit/MockMvc/H2 + PostgreSQL IT
├── postman/                        # Automated HTTP collection + local environment
├── api-requests/                   # Manual IntelliJ .http API examples
├── scripts/                        # Development-only PostgreSQL seed SQL/helpers
├── tickets/                        # Six intended bug/feature exercises
└── docs/                           # Architectural explanation and verification notes
```

**Why retain these folders?**

| Location | Practical purpose | When to use it |
| --- | --- | --- |
| `postman/` | Collection Runner smoke tests and `baseUrl` environment | After starting the HTTP server |
| `api-requests/` | Lightweight manual API exploration without Postman | While debugging endpoints in IntelliJ |
| `scripts/` | Seeds **PostgreSQL only** with known sample records | After creating a disposable PostgreSQL schema; **not** for H2 |
| `tickets/` | Problem descriptions for planned Claude Code lessons | Select a ticket and build a verified reproduction before solving it |
| `docs/architecture.md` | Explains the monolith, database modes and in-process payment/notifications | Understanding the codebase |
| `docs/migration-status.md` | Explains what has and has not been verified relative to the source migration | Before claiming feature parity |

**Ticket caveat:** The six ticket documents are retained as *research inputs*. Some still describe the original .NET multi-service structure or old ports, and TICKET-101's stated decline behavior is **not yet reproduced** by the current Java source. They are **not yet student-ready assignments** and should be reconciled during the subsequent ticket-validation stage. The default tests intentionally do not exercise every ticket defect.

## 9. Common questions and troubleshooting

**Do I need Docker for ordinary coding and JUnit?** No. H2 is the default, the JUnit suite uses it, and Postman works against the H2-backed running app. Docker is necessary only for PostgreSQL exercises.

**Why does `mvn clean verify` not run Postman?** Postman tests are independent live HTTP calls; Maven's default test suite uses JUnit and MockMvc without launching an external app. Import and run the Postman collection after starting RetailCore.

**How do I reset H2 after Postman modifies inventory?** Stop and restart the application. Because H2 is in memory, a fresh Spring application process recreates the seeded data.

**Why are tests passing with known defects?** They verify baseline behavior. Ticket-specific reproductions and deliberately failing tests are a future separate stage; the starter intentionally includes incomplete validation, concurrency risks, inefficient queries and cache issues.

**How do I get a readable test report?** Open `target/surefire-reports/` for standard tests and `target/failsafe-reports/` for PostgreSQL tests. `BUILD SUCCESS` by itself does not prove all business requirements are satisfied.

## 10. Verified vs. pending

In earlier local runs supplied by the project maintainer, **9/9 JUnit tests in the previous revision (reduced to seven in this revision), 1/1 PostgreSQL Testcontainers test and 14/14 Postman assertions** passed. PostgreSQL shutdown previously emitted Hikari/fork warnings; the later local run after adding `@DirtiesContext` showed normal Hikari shutdown. Those are recorded outcomes from a user's local environment, **not tests rerun while editing these docs**.

Pending: full end-to-end PostgreSQL Compose verification for this packaged revision, original ShopFlow behavior audit completion, and independent reproducibility of each of the six teaching tickets.
