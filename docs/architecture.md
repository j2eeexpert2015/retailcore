# RetailCore architecture

Version 1 is a **single deployable Spring Boot monolith**, using feature packages. `RetailCoreApplication` scans all packages, including the scheduled confirmation poller and notification consumer. A future modular-monolith or microservices version should be created as a separate tagged architectural milestone, not mixed into this baseline.

Unlike the starter .NET system, order-to-payment calls are direct Java method calls rather than network HTTP requests. Consequently, tickets mentioning *network failures between services* need scenario review. The fake payment gateway still produces timeouts, and exception swallowing remains available for investigation.

Database selection uses `retailcore.runtime-mode` in `src/main/resources/application.properties`. Spring selects H2 or PostgreSQL datasource properties via profiles. In Docker mode, Compose overrides the property and supplies a database container. Switching a Spring property alone does **not** start Docker.
