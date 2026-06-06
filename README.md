# Trip Planner

An intelligent travel planning platform that automatically generates optimized multi-day itineraries using geospatial clustering and route optimization.

Instead of manually arranging destinations, travelers can provide a list of attractions and trip duration, and the system will generate a structured itinerary by grouping nearby locations, recommending restaurants, and optimizing travel routes.

---

## Why This Project?

Planning a multi-day trip is often inefficient:

* Attractions are scattered across a city or region
* Users manually decide which places belong together
* Travel routes are suboptimal
* Significant time is spent researching nearby restaurants and logistics

This project addresses these challenges through automated itinerary generation powered by clustering algorithms and route optimization.

---

## Key Features

### Intelligent Itinerary Generation

The platform automatically:

* Groups nearby attractions into logical travel sessions
* Creates balanced daily schedules
* Recommends nearby restaurants
* Optimizes visit order to reduce travel time
* Generates a complete multi-day itinerary

### Geospatial Clustering

Attractions are clustered using a customized Balanced K-Means implementation that:

* Groups geographically close destinations
* Produces balanced cluster sizes
* Creates realistic daily workloads
* Minimizes unnecessary transportation

### Route Optimization

The system integrates with Mapbox Optimization API to determine the most efficient visiting sequence.

Benefits include:

* Reduced travel distance
* Less backtracking
* Better time utilization
* Improved user experience

### Collaborative Trip Planning

Trips support multiple members with role-based access control.

Roles:

* Owner
* Editor
* Viewer

Features:

* Shared itineraries
* Member management
* Public and private trips

### Secure Authentication

* Firebase Authentication
* JWT validation
* Cached JWKS verification
* Spring Security integration

---

## How It Works

Given a list of attractions and trip duration:

### 1. Load Places

The system retrieves attraction information from MongoDB.

### 2. Cluster Destinations

A Balanced K-Means algorithm groups attractions into travel sessions.

```text
Number of clusters = days × 2

Day 1:
  Morning Cluster
  Afternoon Cluster

Day 2:
  Morning Cluster
  Afternoon Cluster
```

### 3. Recommend Restaurants

For each cluster:

* Calculate geographic centroid
* Search nearby restaurants using MongoDB geospatial queries
* Select highly-rated candidates

### 4. Optimize Cluster Order

Clusters are arranged using a nearest-neighbor optimization strategy to reduce inter-cluster travel.

### 5. Optimize Visit Sequence

Mapbox Optimization API determines the best order for visiting attractions within each session.

### 6. Generate Final Itinerary

The system produces:

* Daily schedules
* Time slots
* Attraction ordering
* Restaurant recommendations

without modifying any persisted trip data.

---

## Example Generated Schedule

```text
Day 1

09:00 - 10:30   Bai Dinh Pagoda
11:00 - 12:30   Trang An Landscape Complex

13:00 - 14:00   Lunch Restaurant

14:30 - 16:00   Hoa Lu Ancient Capital

17:00 - 18:30   Dinner Restaurant
```

---

## Architecture

```text
                  ┌──────────────┐
                  │   Clients    │
                  └──────┬───────┘
                         │
                         ▼
               ┌─────────────────┐
               │ Spring Boot API │
               └──────┬──────────┘
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
  PostgreSQL      MongoDB        Redis
   Trip Data     Place Data      Cache
                      │
                      ▼
              Clustering Engine
                      │
                      ▼
               Mapbox Routing
                      │
                      ▼
           Generated Itinerary
```

---

## Technical Challenges

### Balanced K-Means Clustering

Traditional K-Means may create uneven clusters.

A customized implementation is used to:

* Balance cluster sizes
* Improve daily workload distribution
* Generate more practical travel plans

### Geospatial Search

MongoDB geospatial indexes enable:

* Radius-based restaurant search
* Distance-aware recommendations
* Fast location queries

### Route Optimization

Mapbox Optimization API is used to:

* Minimize travel distance
* Optimize visit ordering
* Improve itinerary efficiency

### Multi-Database Architecture

The system uses different databases for different workloads:

| Database   | Purpose                    |
| ---------- | -------------------------- |
| PostgreSQL | Trip and user data         |
| MongoDB    | Place and geospatial data  |
| Redis      | Authentication and caching |

---

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA

### Data Layer

* PostgreSQL
* MongoDB
* Redis

### Authentication

* Firebase Authentication
* JWT Verification

### Mapping & Optimization

* Mapbox Optimization API
* Mapbox Routing Services

### Infrastructure

* Docker
* Docker Compose
* Gradle

---

## Project Structure

```text
src/main/java/

├── trip/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
│
├── clustering/
│   ├── service/
│   ├── controller/
│   └── dto/
│
├── place/
│   ├── service/
│   ├── repository/
│   └── model/
│
├── firebase/
│   ├── security/
│   └── service/
│
├── exploration/
│
└── common/
```

---

## Running Locally

### Prerequisites

* Java 17+
* Docker
* PostgreSQL
* Redis
* MongoDB
* Firebase Project
* Mapbox API Key

### Start Infrastructure

```bash
docker-compose up -d
```

### Run Application

```bash
./gradlew bootRun
```

Application starts at:

```text
http://localhost:8080
```

---

## Future Improvements

* AI-powered itinerary generation
* User preference learning
* Budget-aware trip planning
* Hotel recommendations
* Weather-aware scheduling
* Real-time traffic optimization
* Multi-city trip support

---

## Resume Highlights

Key engineering concepts demonstrated by this project:

* Geospatial data processing
* Balanced K-Means clustering
* Route optimization
* Multi-database architecture
* JWT authentication
* Distributed caching
* REST API design
* Third-party service integration

---

## License

MIT License
