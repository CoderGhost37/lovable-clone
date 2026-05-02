# Lovable Clone

Lovable Clone is a full-stack SaaS application monorepo that includes:

- `frontend/` — modern React + TypeScript UI
- `backend/` — standalone Spring Boot monolith
- `distributed-lovable/` — distributed microservices version built with Spring Cloud

The project is designed to demonstrate both monolithic and distributed architectures, AI-assisted development workflows, and production-ready deployment patterns.

## Project Structure

```
lovable-clone/
├── backend/
│   ├── pom.xml
│   ├── mvnw
│   ├── src/
│   └── services.docker-compose.yml
├── distributed-lovable/
│   ├── account-service/
│   ├── api-gateway/
│   ├── common-lib/
│   ├── config-service/
│   ├── discovery-service/
│   ├── intelligence-service/
│   ├── workspace-service/
│   ├── k8s/
│   └── services.docker-compose.yml
└── frontend/
    ├── package.json
    ├── Dockerfile
    ├── src/
    ├── tsconfig.json
    └── vite.config.ts
```

## Key Features

### Frontend

- React 18 + TypeScript UI
- Vite-powered development experience
- Tailwind CSS styling
- CodeMirror-based in-browser code editor
- Project/workspace management UI
- AI chat and assistance panels
- File explorer, previews, and dashboard views

### Backend

- Spring Boot 4.0.0 monolithic service
- Java 21
- Spring Data JPA + PostgreSQL
- Spring Security + JWT
- Stripe SDK integration
- Spring AI OpenAI model support
- MinIO object storage client
- Kubernetes client support

### Distributed Microservices

- Spring Boot 4.0.5
- Spring Cloud 2025.1.1
- Netflix Eureka service discovery
- Spring Cloud Gateway API gateway
- Spring Cloud Config centralized configuration
- OpenFeign-style inter-service communication
- Common shared library (`common-lib`)
- Kafka and MinIO support via Docker Compose
- Jib Maven plugin for container image builds

## Architecture Overview

### Monolithic Backend

The `backend/` folder contains a single Spring Boot application that provides the core API, data access, security, and AI integrations.

### Distributed Microservices

The `distributed-lovable/` folder contains a microservices architecture with separate services for:

- `discovery-service` — Eureka server for service registration and discovery
- `config-service` — centralized Spring Cloud Config server
- `api-gateway` — Spring Cloud Gateway with auth and routing logic
- `account-service` — user account and authentication management
- `workspace-service` — workspace/project management
- `intelligence-service` — AI/LLM-powered intelligence features
- `common-lib` — shared DTOs, security utilities, and common models

These services rely on external infrastructure such as PostgreSQL with PGVector, MinIO storage, and Kafka event streaming.

### Frontend Interaction

The frontend communicates with the backend/API gateway through REST endpoints. The gateway routes authenticated requests to the appropriate microservices, while the React UI handles user interactions, project editing, and AI chat features.

## Local Development

### Prerequisites

- Node.js 20.x
- npm
- Java 21
- Docker (for local infrastructure)
- Maven (`./mvnw` is included for project builds)

### Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Open the browser at the Vite dev server URL shown in the terminal.

### Build the Frontend

```bash
cd frontend
npm run build
```

### Run the Monolithic Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Run Distributed Infrastructure

From the distributed microservices root:

```bash
cd distributed-lovable
docker compose -f services.docker-compose.yml up -d
```

This starts:

- PostgreSQL with PGVector on host port `9010`
- MinIO object storage on ports `9000` and `9001`
- Kafka broker on ports `9092` and `29092`

Then run each microservice with Maven from its directory, for example:

```bash
cd distributed-lovable/account-service
./mvnw spring-boot:run
```

Repeat for `api-gateway`, `config-service`, `discovery-service`, `workspace-service`, and `intelligence-service`.

## Docker and Deployment

### Frontend Docker Image

The frontend Dockerfile is a multi-stage build:

- Build stage: `node:20-alpine`
- Runtime stage: `nginx:alpine`
- Configured for SPA routing via `try_files ... /index.html`

Build and run locally:

```bash
cd frontend
docker build -t lovable-frontend:latest .
docker run -p 80:80 lovable-frontend:latest
```

### Microservices Container Builds

The distributed services use Jib to generate Docker images, for example:

```bash
cd distributed-lovable/account-service
./mvnw package
```

This uses Jib to publish a container image named `docker.io/coderghost37/lovable-account-service:0.0.1-SNAPSHOT`.

### Kubernetes

A `distributed-lovable/k8s/` directory contains manifests for deploying infrastructure, proxy, services, and stateful components. Use these manifests to deploy the service mesh and supporting infrastructure to a Kubernetes cluster.

## Important Notes

- `frontend/Dockerfile` sets `VITE_API_BASE_URL=http://api.lovable.kushagramathur.com` during build.
- The distributed services perform discovery and configuration through Eureka and Spring Cloud Config, so order matters when starting them locally.
- The monolithic backend and distributed microservices are alternate architectures; use the one that best fits your development or demo scenario.

## Recommended Workflows

### Quick Local Prototype

1. Start frontend:
   - `cd frontend && npm install && npm run dev`
2. Start monolithic backend:
   - `cd backend && ./mvnw spring-boot:run`
3. Open the browser and connect the UI to the backend.

### Full Distributed Stack

2. Start discovery and config services
3. Start gateway and service modules
4. Start frontend against the deployed API gateway

## Repository Contents Summary

- `backend/` — Spring Boot monolith with AI, Stripe, MinIO, and Redis support
- `distributed-lovable/` — distributed Spring Cloud microservices
- `frontend/` — React + Vite application with code editor, AI panel, and workspace UI

