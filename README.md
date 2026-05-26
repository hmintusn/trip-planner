# Trip Planner API

## Introduction

Trip Planner is a backend API for collaborative trip planning. It enables users to create and manage trips with multiple members, organize activities across days, and integrate real-world places. The platform supports role-based access control (Owner, Editor, Viewer), uses Firebase for authentication, and provides a structured way to plan group travels.

## Key Capabilities

- Create and manage trips with members and detailed itineraries
- Organize activities by trip days with time scheduling
- Assign roles to trip members (Owner, Editor, Viewer)
- Store trips in PostgreSQL and cache optimization data in Redis
- Integrate places data from MongoDB
- Use Mapbox for trip optimization
- Secure endpoints with Firebase authentication
- Track trip status and visibility (Private, Public)

## Getting Started

### Prerequisites

- Java 17 or later
- Docker & Docker Compose (recommended for full stack)
- Redis running on `localhost:6379`
- PostgreSQL database
- MongoDB Atlas account (for places data)
- Mapbox API key
- Firebase project with Authentication enabled

### Run with Docker

```bash
docker-compose up -d
```

This starts:
- Spring Boot API on `http://localhost:8080`
- PostgreSQL database
- Redis cache
- Application connects to MongoDB Atlas for places data

### Run Locally

1. Update `src/main/resources/application-local.yml` with your Firebase, PostgreSQL, MongoDB, and Mapbox credentials.
2. Start Redis:

```bash
docker run -d -p 6379:6379 redis:alpine
```

3. Run the application:

```bash
./gradlew bootRun
```

## Project Structure

- `trip/` — Trip management (create, update, members, days, activities)
- `user/` — User profiles and authentication
- `place/` — Place data from MongoDB
- `firebase/` — Firebase token verification and JWKS caching
- `exploration/` — Trip exploration and discovery
- `clustering/` — Place clustering logic
- `common/` — Shared configurations and security

## Key Endpoints

### Trip Management
- `POST /api/v1/trips` — Create a new trip with members, days, and activities
- `GET /api/v1/trips` — List user's trips
- `GET /api/v1/trips/{id}` — Get trip details
- `PUT /api/v1/trips/{id}` — Update trip
- `DELETE /api/v1/trips/{id}` — Delete trip

### Members & Activities
- `POST /api/v1/trips/{id}/members` — Add member to trip
- `DELETE /api/v1/trips/{id}/members/{userLocalId}` — Remove member
- `POST /api/v1/trips/{id}/days` — Add day to trip
- `POST /api/v1/trips/{id}/days/{dayId}/activities` — Add activity

### Admin
- `GET /api/v1/admin/trips` — List all trips (admin only)
- `PUT /api/v1/admin/trips/{id}/status` — Update trip status

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Client Applications                         │
│                     (Web, Mobile, Desktop)                           │
└─────────────────────────┬──────────────────────────────────────────┘
                          │
                          │ REST API (HTTP/JWT)
                          │
         ┌────────────────▼────────────────────────────────────────────┐
         │              Spring Boot Backend (Port 8080)                │
         │  ┌──────────────────────────────────────────────────────┐   │
         │  │  REST Controllers                                     │   │
         │  │  - Trip, User, Place, Auth, Admin                    │   │
         │  └──────────────────┬───────────────────────────────────┘   │
         │                     │                                        │
         │  ┌──────────────────▼───────────────────────────────────┐   │
         │  │  Business Logic Services                            │   │
         │  │  - TripService, UserService, PlaceService           │   │
         │  │  - ExplorationService, ClusteringService            │   │
         │  └──────────────────┬───────────────────────────────────┘   │
         │                     │                                        │
         │  ┌──────────────────▼───────────────────────────────────┐   │
         │  │  Security & Authentication                          │   │
         │  │  - FirebaseAuthenticationFilter                     │   │
         │  │  - FirebaseJwksService (token verification)         │   │
         │  │  - SecurityContextHolder                            │   │
         │  └──────────────────────────────────────────────────────┘   │
         └─────────┬──────────────────┬──────────────────┬─────────────┘
                   │                  │                  │
        ┌──────────▼───┐   ┌──────────▼───┐   ┌─────────▼────┐
        │ PostgreSQL   │   │   Redis      │   │  MongoDB     │
        │ (Port 5432)  │   │ (Port 6379)  │   │  (Atlas)     │
        │              │   │              │   │              │
        │ - Trips      │   │ - JWT Cache  │   │ - Places     │
        │ - Users      │   │ - JWKS Cache │   │ - Snapshots  │
        │ - Activities │   │ - Session    │   │ - Metadata   │
        │ - Days       │   │                 │              │
        └──────────────┘   └──────────────┘   └──────────────┘
                   │                  │                  │
                   └──────────────────┬──────────────────┘
                                      │
                          ┌───────────▼────────────┐
                          │  External Services    │
                          │  ┌─────────────────┐  │
                          │  │ Firebase Auth   │  │
                          │  │ - JWKS Endpoint │  │
                          │  │ - User Tokens   │  │
                          │  └─────────────────┘  │
                          │  ┌─────────────────┐  │
                          │  │ Mapbox API      │  │
                          │  │ - Optimization  │  │
                          │  │ - Routing       │  │
                          │  └─────────────────┘  │
                          └────────────────────────┘
```

### Data Flow

1. **Client Request** → REST API with Firebase ID token
2. **Authentication** → FirebaseAuthenticationFilter validates token via JWKS (cached in Redis)
3. **Business Logic** → TripService processes request, enforces role-based access
4. **Data Storage** → 
   - Trip metadata → PostgreSQL
   - Place data → MongoDB
   - Session cache → Redis
5. **Response** → Trip details with members, days, activities

### Key Components

| Component | Responsibility | Storage |
|-----------|-----------------|---------|
| **TripController** | REST endpoints for trip management | N/A |
| **TripService** | Business logic, role enforcement, optimization | N/A |
| **TripRepository** | Query trips, members, days, activities | PostgreSQL |
| **PlaceService** | Place data retrieval and caching | MongoDB |
| **FirebaseJwksService** | Fetch and cache Firebase public keys | Redis |
| **SecurityFilter** | Validate JWT tokens on every request | N/A |

## Example: Create a Trip

```bash
curl -X POST http://localhost:8080/api/v1/trips \
  -H "Authorization: Bearer YOUR_FIREBASE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekend in Hoi An",
    "description": "A relaxing weekend getaway",
    "startDate": "2026-01-10",
    "endDate": "2026-01-12",
    "visibility": "PRIVATE",
    "members": [
      {
        "userLocalId": "friend@example.com",
        "role": "EDITOR"
      }
    ],
    "days": [
      {
        "dayDate": "2026-01-10",
        "notes": "Arrival day",
        "activities": [
          {
            "placeId": "ChIJLfyY2E4rQjERCq-pDhpe4hU",
            "startTime": "14:00",
            "endTime": "16:00",
            "notes": "Check in at hotel"
          }
        ]
      }
    ]
  }'
```

## Technology Stack

- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Authentication**: Firebase Authentication with JWT token verification
- **Databases**: PostgreSQL (trips, users), MongoDB (places), Redis (caching)
- **Mapping**: Mapbox for trip optimization
- **DevOps**: Docker, Docker Compose
- **Build**: Gradle

## Configuration

Key settings in `src/main/resources/application.yml`:

- Firebase project ID and JWKS URL
- PostgreSQL connection string
- MongoDB Atlas connection
- Redis host and port
- Mapbox API token

---

For detailed API documentation and examples, see the files under `/doc`.


