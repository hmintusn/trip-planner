# 🧩 Spring Boot Firebase JWKS Fetcher & Verifier (with Redis Cache)

## Overview

This project demonstrates best practices for integrating **Firebase Authentication** in a **Spring Boot** backend. It securely fetches Firebase JWKS (JSON Web Key Sets), caches them in Redis, and verifies Firebase ID tokens efficiently while handling key rotation automatically.

## 🎯 Features

- ✅ Fetch Firebase public keys (JWKS) from the official endpoint
- ✅ Cache JWKS in Redis with proper TTL
- ✅ Auto-refresh on key rotation or cache expiry
- ✅ Reusable service for token verification
- ✅ Spring Security integration
- ✅ Comprehensive error handling
- ✅ Monitoring and health check endpoints

## 🏗️ Project Structure

```
com/example/trip_planner/
 ├── common/                 # Shared utilities
 │    ├── config/            # Redis, Security configurations
 │    ├── security/          # JWT filters and authentication
 │    ├── util/              # Helper utilities (Redis, JSON)
 │    └── constants/         # Application constants
 ├── firebase/               # Firebase integration
 │    ├── service/           # JWKS fetch & token verification
 │    └── model/             # Data models
 └── user/                   # User domain
      ├── controller/        # REST endpoints
      └── service/           # Business logic
```

## 🚀 Quick Start

### Prerequisites

1. **Java 17+**
2. **Redis Server** running on localhost:6379
3. **Firebase Project** with Authentication enabled

### 1. Configure Firebase Project

Update `src/main/resources/application.yml`:

```yaml
firebase:
  project-id: your-actual-firebase-project-id  # Replace with your Firebase project ID
```

### 2. Start Redis

```bash
# Using Docker
docker run -d -p 6379:6379 redis:alpine

# Or install Redis locally and start the service
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 🧪 Testing the Implementation

### 1. Health Check (No Authentication Required)

```bash
curl http://localhost:8080/api/public/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "trip-planner",
  "timestamp": 1699123456789
}
```

### 2. JWKS Information (No Authentication Required)

```bash
curl http://localhost:8080/api/auth/public/jwks
```

This endpoint will:
- Fetch JWKS from Firebase if not cached
- Cache the keys in Redis with TTL
- Return cache metadata and key information

### 3. Test Firebase Token Verification

To test with a real Firebase token, you'll need to:

1. **Get a Firebase ID Token** from your frontend application
2. **Use it in authenticated requests**:

```bash
curl -H "Authorization: Bearer YOUR_FIREBASE_ID_TOKEN" \
     http://localhost:8080/api/auth/profile
```

Expected response:
```json
{
  "userId": "firebase-user-id",
  "userEmail": "user@example.com",
  "tokenIssuedAt": "2023-11-04T10:30:00Z",
  "tokenExpiresAt": "2023-11-04T11:30:00Z",
  "tokenIssuer": "https://securetoken.google.com/your-project-id",
  "tokenAudience": "your-project-id"
}
```

### 4. Cache Management

**Force refresh JWKS:**
```bash
curl -X POST http://localhost:8080/api/auth/public/refresh-jwks
```

**Clear cache:**
```bash
curl -X DELETE http://localhost:8080/api/auth/public/clear-cache
```

## 🔧 Configuration

### Redis Configuration

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

### Firebase Configuration

```yaml
firebase:
  jwks-url: https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com
  cache-ttl-seconds: 3600  # Default TTL if Firebase doesn't specify
  project-id: your-firebase-project-id
```

## 🛡️ Security Features

- **JWT Token Verification**: Validates Firebase ID tokens
- **Key Rotation Handling**: Automatically refreshes keys on signature failures
- **Redis Caching**: Reduces Firebase API calls
- **TTL Management**: Respects Firebase Cache-Control headers
- **Error Handling**: Comprehensive error responses

## 📊 Monitoring

The application provides several monitoring endpoints:

- `/api/public/health` - Application health
- `/api/auth/public/jwks` - JWKS cache status
- `/api/auth/jwks-status` - JWKS status (authenticated)

## 🏃‍♂️ Development

### Build
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Run Application
```bash
./gradlew bootRun
```

## 🔍 Key Components

### FirebaseJwksService
- Fetches JWKS from Firebase
- Caches keys in Redis with TTL
- Handles cache expiration and refresh

### FirebaseTokenVerifier
- Verifies Firebase ID tokens
- Uses cached JWKS for signature validation
- Handles key rotation automatically

### FirebaseAuthenticationFilter
- Spring Security filter for JWT authentication
- Extracts user information from tokens
- Sets security context for downstream services

## 🚨 Troubleshooting

### Redis Connection Issues
- Ensure Redis is running on localhost:6379
- Check Redis logs for connection errors

### Firebase Token Verification Fails
- Verify Firebase project ID is correct
- Check if token is expired
- Ensure Firebase project has Authentication enabled

### JWKS Fetching Issues
- Check internet connectivity
- Verify Firebase JWKS URL is accessible
- Check application logs for detailed error messages

## 📚 References

- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin/setup)
- [Firebase Public Keys Endpoint](https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com)
- [Spring Boot Redis Guide](https://docs.spring.io/spring-boot/reference/data/nosql/redis.html)
- [Spring Security JWT Guide](https://spring.io/guides/topicals/spring-security-architecture/)
