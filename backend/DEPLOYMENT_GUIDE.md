# Lovable Clone Monolith - Deployment Guide

This guide covers running the Lovable Clone monolithic Spring Boot application both locally and deploying it to production environments.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Local Development Setup](#local-development-setup)
- [Running Locally](#running-locally)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)
- [API Documentation](#api-documentation)

## Architecture Overview

The Lovable Clone backend is a **monolithic Spring Boot 4.0** application that combines all functionalities in a single deployable unit.

### Technology Stack

- **Framework**: Spring Boot 4.0.0
- **Java Version**: 21
- **Build Tool**: Maven 3.9+ with Maven Wrapper
- **Database**: PostgreSQL 18 with pgvector extension
- **Object Storage**: MinIO
- **Cache**: Redis 7
- **AI Integration**: Spring AI 2.0.0-M1 (OpenAI GPT-4)
- **Payment**: Stripe API
- **Container Orchestration**: Kubernetes (Fabric8 client)

### Core Components

| Component | Description |
|-----------|-------------|
| **Auth Module** | User registration, login, JWT authentication |
| **Project Management** | Create, edit, manage projects and files |
| **File Storage** | MinIO integration for file uploads/downloads |
| **AI Chat** | OpenAI integration for code generation |
| **Billing** | Stripe integration for subscriptions |
| **Preview System** | Kubernetes pod deployment for live previews |
| **Team Collaboration** | Project member management and permissions |

### Application Modules

```
backend/
├── src/main/java/com/kushagramathur/lovable_clone/
│   ├── controller/           # 7 REST controllers
│   │   ├── AuthController.java         # /auth/** - User authentication
│   │   ├── ProjectController.java      # /projects/** - Project CRUD
│   │   ├── FileController.java         # /files/** - File operations
│   │   ├── ChatController.java         # /chat/** - AI chat/generation
│   │   ├── BillingController.java      # /billing/** - Stripe integration
│   │   ├── ProjectMemberController.java # /members/** - Team management
│   │   └── UsageController.java        # /usage/** - Usage tracking
│   ├── entity/               # 13 JPA entities
│   │   ├── User.java
│   │   ├── Project.java
│   │   ├── ProjectFile.java
│   │   ├── ProjectMember.java
│   │   ├── Preview.java (K8s pod mapping)
│   │   ├── Plan.java
│   │   ├── Subscription.java
│   │   ├── ChatSession.java
│   │   ├── ChatMessage.java
│   │   ├── ChatEvent.java
│   │   └── UsageLog.java
│   ├── service/              # Business logic layer
│   ├── repository/           # Spring Data JPA repositories
│   ├── security/             # JWT, SecurityConfig
│   ├── config/               # App configuration
│   │   ├── MinioConfig.java
│   │   ├── KubernetesConfig.java
│   │   └── RedisConfig.java
│   ├── llm/                  # AI integration
│   │   ├── tools/            # Spring AI tools
│   │   └── advisors/         # Context advisors
│   └── dto/                  # Request/Response DTOs
├── pom.xml                   # Maven dependencies
├── services.docker-compose.yml # Local services
└── k8s/                      # Kubernetes manifests

```

### Key Features

1. **Unified Codebase**: All features in one application
2. **JWT Authentication**: Stateless authentication across all endpoints
3. **AI-Powered**: OpenAI GPT-4 integration via Spring AI
4. **File Management**: MinIO object storage integration
5. **Real-time Previews**: Dynamic Kubernetes pod creation
6. **Subscription Billing**: Full Stripe integration
7. **Team Collaboration**: Multi-user project support

## Prerequisites

### Required Software

1. **Java 21**
   ```bash
   # Check version
   java -version

   # Should output: openjdk version "21.x.x"
   ```
   Download from: https://adoptium.net/

2. **Maven 3.9+** (or use included Maven Wrapper)
   ```bash
   # Check version
   mvn -version
   ```

3. **Docker & Docker Compose**
   ```bash
   # Check versions
   docker --version
   docker-compose --version
   ```
   Download from: https://www.docker.com/get-started

4. **PostgreSQL Client** (optional, for debugging)
   ```bash
   psql --version
   ```

### Required Accounts & API Keys

1. **OpenAI API Key** - For AI code generation
   - Get from: https://platform.openai.com/api-keys
   - Model used: GPT-4 (gpt-4o)

2. **Stripe Account** - For subscription billing
   - Get from: https://dashboard.stripe.com/apikeys
   - Requires both API key and webhook secret

3. **MinIO/S3** - Object storage (local or cloud)
   - Local: Runs via Docker Compose
   - Cloud: AWS S3, MinIO Cloud, etc.

## Local Development Setup

### Step 1: Clone the Repository

```bash
cd lovable-clone/backend
```

### Step 2: Start Supporting Services

Start PostgreSQL and MinIO using Docker Compose:

```bash
# Start services in detached mode
docker-compose -f services.docker-compose.yml up -d

# Check services are running
docker-compose -f services.docker-compose.yml ps
```

This starts:
- **PostgreSQL with pgvector**: `localhost:9010`
- **MinIO API**: `localhost:9000`
- **MinIO Console**: `localhost:9001`

Access MinIO Console at http://localhost:9001:
- Username: `minioadmin`
- Password: `minioadmin123`

### Step 3: Configure Application Properties

Edit `src/main/resources/application.yaml` or create an override file:

```yaml
spring:
  application:
    name: lovable-clone

  datasource:
    url: jdbc:postgresql://localhost:9010/db
    username: user
    password: password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update  # Auto-create tables
    show-sql: true      # Log SQL queries

  data:
    redis:
      host: localhost
      port: 6379

  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # Set via environment variable
      chat:
        options:
          model: gpt-4o
          temperature: 0.0

jwt:
  secret-key: ${JWT_SECRET:default-secret-key-change-in-production}

stripe:
  api:
    secret: ${STRIPE_API_KEY}
  webhook:
    secret: ${STRIPE_WEBHOOK_SECRET}

client:
  url: http://localhost:8080  # Frontend URL

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin123
  project-bucket: projects
```

### Step 4: Set Environment Variables

Create a `.env` file in the `backend/` directory:

```env
# OpenAI Configuration
OPENAI_API_KEY=sk-your-openai-api-key-here

# JWT Secret (generate with: openssl rand -base64 32)
JWT_SECRET=your-jwt-secret-key-here

# Stripe Configuration
STRIPE_API_KEY=sk_test_your-stripe-secret-key
STRIPE_WEBHOOK_SECRET=whsec_your-webhook-secret

# Frontend URL
CLIENT_URL=http://localhost:8080
```

**Generate strong JWT secret:**
```bash
openssl rand -base64 32
```

### Step 5: Build the Application

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

This will:
- Download all dependencies
- Compile Java sources
- Process Lombok annotations
- Process MapStruct mappers
- Run tests (if any)
- Package the application

## Running Locally

### Option 1: Using Maven (Development Mode)

```bash
# Run with default profile
./mvnw spring-boot:run

# Run with environment variables
OPENAI_API_KEY=sk-xxx STRIPE_API_KEY=sk_test_xxx ./mvnw spring-boot:run

# Run with custom port
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

The application starts at: **http://localhost:8080**

### Option 2: Using JAR File (Production-like)

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/lovable-clone-0.0.1-SNAPSHOT.jar

# With environment variables
OPENAI_API_KEY=sk-xxx java -jar target/lovable-clone-0.0.1-SNAPSHOT.jar
```

### Option 3: Using IDE

**IntelliJ IDEA / Eclipse:**
1. Import as Maven project
2. Set environment variables in Run Configuration
3. Run `LovableCloneApplication.java` main class

### Verify Application is Running

```bash
# Health check (if actuator is enabled)
curl http://localhost:8080/actuator/health

# Check API endpoints
curl http://localhost:8080/auth/status  # Should return 401 or method not allowed
```

### View Logs

```bash
# Maven run shows logs in console
# For JAR file, logs are printed to stdout

# Save logs to file
./mvnw spring-boot:run > app.log 2>&1 &
```

## Configuration

### Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `OPENAI_API_KEY` | Yes | None | OpenAI API key for GPT-4 |
| `JWT_SECRET` | Yes | (dev default) | Secret key for JWT signing |
| `STRIPE_API_KEY` | Yes | None | Stripe secret API key |
| `STRIPE_WEBHOOK_SECRET` | Yes | None | Stripe webhook signing secret |
| `CLIENT_URL` | No | http://localhost:8080 | Frontend application URL |
| `SERVER_PORT` | No | 8080 | Application server port |
| `SPRING_PROFILES_ACTIVE` | No | default | Active Spring profiles |

### Database Configuration

**Local (Docker Compose):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:9010/db
    username: user
    password: password
```

**Production (External PostgreSQL):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/lovable_db
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

### MinIO Configuration

**Local:**
```yaml
minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin123
```

**Production (S3-compatible):**
```yaml
minio:
  url: https://s3.amazonaws.com
  access-key: ${AWS_ACCESS_KEY}
  secret-key: ${AWS_SECRET_KEY}
  project-bucket: lovable-projects
```

### Redis Configuration

**Local:**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

**Production:**
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
```

## Docker Deployment

### Create Dockerfile

Create `Dockerfile` in the `backend/` directory:

```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/lovable-clone-*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build Docker Image

```bash
# Build image
docker build -t lovable-clone-backend:latest .

# Build with specific tag
docker build -t yourusername/lovable-clone:v1.0.0 .
```

### Run Docker Container

```bash
# Run with environment variables
docker run -d \
  --name lovable-backend \
  -p 8080:8080 \
  -e OPENAI_API_KEY=sk-xxx \
  -e JWT_SECRET=your-secret \
  -e STRIPE_API_KEY=sk_test_xxx \
  -e STRIPE_WEBHOOK_SECRET=whsec_xxx \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:9010/db \
  lovable-clone-backend:latest

# View logs
docker logs -f lovable-backend

# Stop container
docker stop lovable-backend
```

### Docker Compose (Full Stack)

Create `docker-compose.full.yml`:

```yaml
version: '3.9'

services:
  pgvector:
    image: pgvector/pgvector:0.8.1-pg18-trixie
    environment:
      POSTGRES_DB: db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "9010:5432"
    volumes:
      - pgvector-data:/var/lib/postgresql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user -d db"]
      interval: 10s
      timeout: 5s
      retries: 5

  minio:
    image: quay.io/minio/minio:latest
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin123
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio-data:/data
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  backend:
    build: .
    depends_on:
      pgvector:
        condition: service_healthy
      minio:
        condition: service_healthy
      redis:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://pgvector:5432/db
      - SPRING_DATASOURCE_USERNAME=user
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
      - MINIO_URL=http://minio:9000
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - JWT_SECRET=${JWT_SECRET}
      - STRIPE_API_KEY=${STRIPE_API_KEY}
      - STRIPE_WEBHOOK_SECRET=${STRIPE_WEBHOOK_SECRET}
    restart: unless-stopped

volumes:
  pgvector-data:
  minio-data:
```

**Run full stack:**
```bash
docker-compose -f docker-compose.full.yml up -d
```

## Kubernetes Deployment

### Prerequisites for K8s Deployment

1. **Kubernetes cluster** (GKE, EKS, AKS, or local k3s/minikube)
2. **kubectl** configured
3. **Docker image** pushed to registry

### Step 1: Push Docker Image to Registry

```bash
# Tag image
docker tag lovable-clone-backend:latest yourusername/lovable-clone:latest

# Login to Docker Hub
docker login

# Push image
docker push yourusername/lovable-clone:latest
```

### Step 2: Create Kubernetes Manifests

Create `k8s/deployment.yml`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: lovable

---
apiVersion: v1
kind: ConfigMap
metadata:
  name: lovable-config
  namespace: lovable
data:
  CLIENT_URL: "http://lovable.example.com"
  MINIO_URL: "http://minio:9000"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/lovable_db"

---
apiVersion: v1
kind: Secret
metadata:
  name: lovable-secrets
  namespace: lovable
type: Opaque
stringData:
  OPENAI_API_KEY: "sk-your-key-here"
  JWT_SECRET: "your-jwt-secret-here"
  STRIPE_API_KEY: "sk_test_your-key"
  STRIPE_WEBHOOK_SECRET: "whsec_your-secret"
  DB_USERNAME: "lovable_user"
  DB_PASSWORD: "your-db-password"
  MINIO_ACCESS_KEY: "minioadmin"
  MINIO_SECRET_KEY: "minioadmin123"

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lovable-backend
  namespace: lovable
spec:
  replicas: 2
  selector:
    matchLabels:
      app: lovable-backend
  template:
    metadata:
      labels:
        app: lovable-backend
    spec:
      containers:
      - name: backend
        image: yourusername/lovable-clone:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: lovable-config
              key: SPRING_DATASOURCE_URL
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: DB_USERNAME
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: DB_PASSWORD
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: OPENAI_API_KEY
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: JWT_SECRET
        - name: STRIPE_API_KEY
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: STRIPE_API_KEY
        - name: STRIPE_WEBHOOK_SECRET
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: STRIPE_WEBHOOK_SECRET
        - name: MINIO_URL
          valueFrom:
            configMapKeyRef:
              name: lovable-config
              key: MINIO_URL
        - name: MINIO_ACCESS_KEY
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: MINIO_ACCESS_KEY
        - name: MINIO_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: lovable-secrets
              key: MINIO_SECRET_KEY
        - name: CLIENT_URL
          valueFrom:
            configMapKeyRef:
              name: lovable-config
              key: CLIENT_URL
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 120
          periodSeconds: 20

---
apiVersion: v1
kind: Service
metadata:
  name: lovable-backend
  namespace: lovable
spec:
  selector:
    app: lovable-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

### Step 3: Deploy to Kubernetes

```bash
# Apply manifests
kubectl apply -f k8s/deployment.yml

# Check deployment
kubectl get pods -n lovable
kubectl get svc -n lovable

# View logs
kubectl logs -f deployment/lovable-backend -n lovable

# Get external IP (for LoadBalancer)
kubectl get svc lovable-backend -n lovable
```

### Step 4: Deploy Supporting Services

You'll also need to deploy PostgreSQL, MinIO, and Redis to Kubernetes. Use the existing manifests from `k8s/infra.yml` or refer to the distributed-lovable deployment guide.

## Production Deployment

### Checklist

- [ ] Use production database (not Docker Compose)
- [ ] Configure proper secrets management (K8s Secrets, HashiCorp Vault, GCP Secret Manager)
- [ ] Enable HTTPS/TLS
- [ ] Set up monitoring (Prometheus, Grafana)
- [ ] Configure logging (ELK stack, Loki)
- [ ] Set up backups (database, MinIO)
- [ ] Configure auto-scaling (HPA)
- [ ] Set resource limits appropriately
- [ ] Use production-grade JWT secret (min 256 bits)
- [ ] Enable production Stripe keys
- [ ] Set up health checks and alerts
- [ ] Configure CORS properly for production domain

### Production Environment Variables

```yaml
# Use environment-specific profiles
SPRING_PROFILES_ACTIVE=prod

# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/lovable_prod
SPRING_DATASOURCE_USERNAME=lovable_prod_user
SPRING_DATASOURCE_PASSWORD=<strong-password>

# JPA settings
SPRING_JPA_HIBERNATE_DDL_AUTO=validate  # Never use 'update' in prod!
SPRING_JPA_SHOW_SQL=false                # Disable SQL logging

# Redis
SPRING_DATA_REDIS_HOST=prod-redis
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=<redis-password>

# MinIO/S3
MINIO_URL=https://s3.amazonaws.com
MINIO_ACCESS_KEY=<aws-access-key>
MINIO_SECRET_KEY=<aws-secret-key>

# Security
JWT_SECRET=<strong-256-bit-secret>

# APIs
OPENAI_API_KEY=<production-key>
STRIPE_API_KEY=sk_live_<production-key>
STRIPE_WEBHOOK_SECRET=whsec_<production-secret>

# Application
CLIENT_URL=https://lovable.example.com
```

### Security Best Practices

1. **Never commit secrets to Git**
2. **Use strong, randomly generated secrets**
3. **Rotate secrets regularly**
4. **Use HTTPS everywhere**
5. **Enable CORS only for trusted domains**
6. **Implement rate limiting**
7. **Enable Spring Security's CSRF protection**
8. **Use database connection pooling**
9. **Set appropriate session timeouts**
10. **Enable database SSL connections**

### Monitoring & Observability

**Add Spring Boot Actuator:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Configure actuator endpoints:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## Troubleshooting

### Common Issues

#### 1. Application Won't Start

**Error**: `Cannot load driver class: org.postgresql.Driver`

**Solution**:
```bash
# Rebuild with dependencies
./mvnw clean install
```

#### 2. Database Connection Failed

**Error**: `Connection refused: localhost:9010`

**Solution**:
```bash
# Check if PostgreSQL is running
docker-compose -f services.docker-compose.yml ps

# Start services
docker-compose -f services.docker-compose.yml up -d

# Check logs
docker-compose -f services.docker-compose.yml logs pgvector
```

#### 3. OpenAI API Errors

**Error**: `401 Unauthorized` from OpenAI

**Solution**:
- Verify `OPENAI_API_KEY` is set correctly
- Check API key is valid at https://platform.openai.com/api-keys
- Ensure you have credits in your OpenAI account

#### 4. MinIO Connection Failed

**Error**: `Connection refused: localhost:9000`

**Solution**:
```bash
# Check MinIO is running
docker ps | grep minio

# Restart MinIO
docker-compose -f services.docker-compose.yml restart minio

# Check MinIO logs
docker logs minio-lovable
```

#### 5. Port Already in Use

**Error**: `Port 8080 is already in use`

**Solution**:
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or run on different port
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### 6. Out of Memory Error

**Error**: `java.lang.OutOfMemoryError: Java heap space`

**Solution**:
```bash
# Increase heap size
java -Xmx1024m -Xms512m -jar target/lovable-clone-0.0.1-SNAPSHOT.jar

# Or with Maven
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m -Xms512m"
```

### Logging

**Enable debug logging:**

```yaml
logging:
  level:
    root: INFO
    com.kushagramathur.lovable_clone: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Database Debugging

```bash
# Connect to PostgreSQL
docker exec -it pgvector-db-lovable psql -U user -d db

# List tables
\dt

# Check users
SELECT * FROM users;

# Check projects
SELECT * FROM projects;

# Exit
\q
```

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/signup` | Register new user | No |
| POST | `/auth/login` | Login user | No |
| GET | `/auth/profile` | Get user profile | Yes |

### Project Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/projects` | List user's projects | Yes |
| POST | `/projects` | Create new project | Yes |
| GET | `/projects/{id}` | Get project details | Yes |
| PUT | `/projects/{id}` | Update project | Yes |
| DELETE | `/projects/{id}` | Delete project | Yes |

### File Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/files/tree/{projectId}` | Get file tree | Yes |
| GET | `/files/{projectId}/{path}` | Get file content | Yes |
| POST | `/files/{projectId}` | Create/update file | Yes |
| DELETE | `/files/{projectId}/{path}` | Delete file | Yes |

### Chat/AI Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/chat` | Send chat message | Yes |
| GET | `/chat/sessions` | List chat sessions | Yes |
| GET | `/chat/sessions/{id}` | Get session history | Yes |

### Billing Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/billing/plans` | List available plans | No |
| POST | `/billing/checkout` | Create checkout session | Yes |
| GET | `/billing/subscription` | Get user subscription | Yes |
| POST | `/billing/portal` | Get customer portal | Yes |
| POST | `/webhooks/stripe` | Stripe webhook handler | No (signed) |

### Response Format

**Success Response:**
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful"
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "Error message here",
  "code": "ERROR_CODE"
}
```

---

## Quick Start Summary

```bash
# 1. Start services
cd backend
docker-compose -f services.docker-compose.yml up -d

# 2. Set environment variables
export OPENAI_API_KEY=sk-your-key
export JWT_SECRET=$(openssl rand -base64 32)
export STRIPE_API_KEY=sk_test_your-key
export STRIPE_WEBHOOK_SECRET=whsec_your-secret

# 3. Build and run
./mvnw spring-boot:run

# 4. Test
curl http://localhost:8080/auth/status

# Application is now running at http://localhost:8080
```

---

**For production deployment, always follow the security best practices and use proper secrets management!**
