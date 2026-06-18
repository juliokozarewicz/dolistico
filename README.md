<div align="center">

# 📅 Dolistico

**Personal Task & Event Scheduling API — Microservices Architecture**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-4D4D4D?style=for-the-badge&logo=keycloak&logoColor=white)

</div>

---

## About

**Dolistico** is a REST API for personal task and event scheduling — built for individuals who want a reliable backend to manage their agenda, track to-dos, and organize events in one place.

Under the hood, it runs on a microservices architecture designed with a strong focus on performance, scalability, and security. Services communicate asynchronously through Apache Kafka, ensuring loose coupling and high resilience across the platform.

The codebase is structured around Clean Architecture and Domain-Driven Design principles, keeping business logic isolated from infrastructure concerns and making the system easy to extend and maintain over time. Each microservice owns its own domain, data, and lifecycle — from account management and authentication to email dispatch and scheduling.

Security is treated as a first-class concern: all requests are authenticated via JWT with Refresh Token support, identity is managed through Keycloak, and sensitive data is protected with advanced encryption. A rate limiter sits at the gateway level to guard against abuse and DDoS attempts.

Observability is built in from the ground up, with structured logging ready for integration with modern monitoring stacks. Well-defined test and production profiles, combined with Kubernetes deployment using Kustomize overlays, make the transition from local development to production straightforward and reliable.

---

## Architecture

- **Microservices** with API Gateway via **NGINX**
- **Event-driven** with **Apache Kafka**
- **Clean Architecture** + **Domain-Driven Design (DDD)**
- **Clean Code**
- Well-defined **test** and **production** profiles

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java | Primary language |
| Spring | Core framework |
| Hibernate + Spring Data JPA | Persistence layer |
| Flyway | Database migrations |
| Lombok | Boilerplate reduction |

### Databases
| Technology | Purpose |
|---|---|
| PostgreSQL | Primary database |
| H2 Database | Testing |
| Redis | Caching |

### Security
| Technology | Purpose |
|---|---|
| JWT + Refresh Token | Authentication |
| Keycloak | Identity management |
| Advanced encryption | Sensitive data protection |
| Rate Limiter | DDoS protection |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker | Containerization |
| Docker Compose | Local environment |
| Kubernetes | Deploy (dev and prod with Kustomize) |
| NGINX | API Gateway |

---

## Microservices

### 🔐 Account Service
Handles user management and authentication, integrated with Keycloak.

### 📧 Email Service
Responsible for sending transactional emails, triggered asynchronously via Kafka events.

---

## Quality & Observability

- **Testing:** JUnit · Mockito · JaCoCo (code coverage)
- **Structured logging** ready for integration with modern monitoring tools
- **Error Handler** with robust, centralized error management
- **Input validation** on all endpoints

---

## Documentation

- **OpenAPI** — full endpoint documentation
- **UML** — Sequence, Class, and Activity diagrams

---

## Running Locally

### Prerequisites

- Docker and Docker Compose installed

### Starting the environment

```bash
docker compose up -d
```

The API will be available at `http://localhost:8080`.

The OpenAPI documentation will be available at `http://localhost:8080/swagger-ui.html`.

---

## Deploy

Deployment is handled with **Kubernetes**, using **Kustomize** to manage environment profiles:

```bash
# Development environment
kubectl apply -k k8s/overlays/dev

# Production environment
kubectl apply -k k8s/overlays/prod
```

---

<div align="center">
  <sub>Dolistico — Built with Java & Spring</sub>
</div>