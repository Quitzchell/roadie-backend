# Roadie

A Spring Boot backend for tracking the production side of live shows. Built by a touring musician turned developer.

## Why

Anyone who plays live music knows the drill: every show comes with a pile of production details. Get-in times,
soundcheck slots, venue contact persons, backline arrangements, load-in instructions, parking, catering, curfew. In
practice this information lives scattered across email threads, WhatsApp messages and loose PDFs. Handing production
tasks over within a band is painful, and finding the right detail on the day of the show is worse.

Roadie is my attempt to put all of that into a single application. The domain model is based on real-world itinerary
documents from shows I've played myself.

For the current implementation state and the planned functional and technical roadmap, see [
`docs/roadmap.md`](docs/roadmap.md).

## Tech stack

- Java 21
- Spring Boot 3.5.11
- Spring Web, Spring Data JPA, Spring Security, Bean Validation
- PostgreSQL
- Lombok
- JUnit 5, Spring Boot Test, MockMvc
- Maven

## Architecture

Standard layered Spring Boot, applied consistently across every domain:

```
Controller  -> HTTP layer, validation, status codes
Service     -> business logic, repository orchestration, exception translation
Repository  -> Spring Data JPA
Entity      -> JPA-annotated domain model
DTO         -> Request / Response as Java records
Mapper      -> entity <-> DTO conversion
```

Cross-cutting concerns:

- `GlobalExceptionHandler` translates domain exceptions into problem responses
- Entities override `equals` and `hashCode` using the id-with-null-guard approach recommended for JPA, so entity
  equality stays stable across the Hibernate lifecycle
- DTOs are Java records, enforcing immutability at the API boundary
- Contact roles are stored as an `@ElementCollection` of enums in a dedicated join table

## API

Base path: `/api/v1`

Standard CRUD for each domain:

```
GET    /api/v1/bands
POST   /api/v1/bands
GET    /api/v1/bands/{id}
PUT    /api/v1/bands/{id}
DELETE /api/v1/bands/{id}
```

The same pattern applies to `/venues`, `/locations`, `/contacts` and `/productions`.

## Tests

Two layers:

- **Service-level unit tests** with Mockito for business logic in isolation.
- **Integration tests** (`IntegrationTestBase`) that spin up the full Spring context, connect to a real PostgreSQL
  instance, and create a dedicated UUID-named database per test class via `DynamicPropertySource`. After the test class
  finishes, the database is dropped. This removes cross-test state issues and tests the actual JPA layer end-to-end
  instead of mocking it away.

Run tests:

```bash
./mvnw test
```

A PostgreSQL instance must be reachable at `localhost:5433` with credentials `postgres/postgres` for integration tests
to run. A `docker-compose.yml` that provides exactly this is on the technical roadmap.

## Running locally

> Note: the main profile currently uses `spring.jpa.hibernate.ddl-auto=validate` and there are no Flyway migrations yet,
> so `mvn spring-boot:run` will not start cleanly on an empty database. This is explicitly on the technical roadmap and
> will be fixed shortly.

Once Flyway and `docker-compose.yml` are in place, the setup will be:

```bash
docker compose up -d
./mvnw spring-boot:run
```

## About

I'm Mitchell Quitz, a full-stack developer at a Dutch digital agency, currently transitioning from PHP and Laravel to
Java. I've played in touring bands for years, which is where this project comes from. Roadie is both a real itch I
wanted to scratch and the vehicle I'm using to learn Spring Boot properly.

- LinkedIn: [linkedin.com/in/mitchellquitz](https://www.linkedin.com/in/mitchellquitz)
- GitHub: [github.com/Quitzchell](https://github.com/Quitzchell)
