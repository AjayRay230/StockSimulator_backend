
# 📈 Stock Simulator – Backend

> A transactional, event-driven trading engine built with Spring Boot
> Implements limit order matching, partial fills, real-time updates, and concurrency-safe trade settlement.

---

# 🏗 Architecture Overview

The backend simulates a simplified stock exchange with:

* Market order execution
* Limit order book
* Price-time priority matching engine
* Partial fills
* Atomic trade settlement
* Event-driven post-processing
* Real-time WebSocket updates

---

#  High-Level System Architecture

```
                         ┌───────────────────────┐
                         │       Frontend        │
                         │ (React Admin + User)  │
                         └─────────────┬─────────┘
                                       │
                                       ▼
                           ┌───────────────────┐
                           │  REST Controllers │
                           └─────────┬─────────┘
                                     │
                                     ▼
                         ┌────────────────────────┐
                         │     Service Layer      │
                         │────────────────────────│
                         │ TransactionService     │
                         │ LimitOrderService      │
                         │ Analytics Services     │
                         └─────────┬──────────────┘
                                   │
        ┌──────────────────────────┼──────────────────────────┐
        ▼                          ▼                          ▼
┌─────────────────┐     ┌─────────────────┐        ┌─────────────────┐
│ Matching Engine │     │ Trade Settlement│        │ Event Publisher │
│ (Price-Time)    │     │ (Atomic)        │        │ (Spring Events) │
└────────┬────────┘     └────────┬────────┘        └────────┬────────┘
         │                        │                           │
         ▼                        ▼                           ▼
 ┌────────────────┐      ┌────────────────┐          ┌────────────────┐
 │ LimitOrderRepo │      │ TransactionRepo│          │ Event Listeners│
 │ (Order Book)   │      │ PortfolioRepo  │          │ Leaderboard    │
 └────────┬───────┘      └────────┬───────┘          │ Metrics        │
          │                        │                  └────────────────┘
          ▼                        ▼
                  ┌────────────────────────┐
                  │       MySQL DB         │
                  └────────────────────────┘
```

---

# 🔁 Trade Execution Flows

---

## 1️⃣ Market Order Flow

```
Client
  ↓
TransactionService.buyStock() / sellStock()
  ↓
Balance Update
  ↓
Portfolio Update
  ↓
Transaction Record
  ↓
TradePlacedEvent Published
  ↓
Listeners (Leaderboard, Metrics)
```

---

## 2️⃣ Limit Order Matching Engine

```
User places LIMIT order
        ↓
Stored as PENDING in order book
        ↓
Scheduled Matching Engine
        ↓
Sort by Price-Time Priority
        ↓
Partial Fill Handling
        ↓
settleMatchedTrade()
        ↓
Atomic Settlement
        ↓
Two Transaction Records Created
        ↓
TradePlacedEvent Published
```

---

# ⚙ Matching Engine Design

### ✔ Price-Time Priority

Buy Orders:

* Highest price first
* Earliest timestamp first

Sell Orders:

* Lowest price first
* Earliest timestamp first

Matching condition:

```
BUY.price >= SELL.price
```

Execution price:

* Uses passive order price (sell side)

---

### ✔ Partial Fill Support

Each `LimitOrder` contains:

```
quantity
remainingQuantity
status (PENDING / PARTIAL / EXECUTED / CANCELLED)
```

If a full match is not possible:

* remainingQuantity is reduced
* status becomes PARTIAL
* Matching continues

---

# 🔒 Concurrency & Consistency Strategy

### ✔ Optimistic Locking

```
@Version
private Long version;
```

Prevents:

* Double execution
* Lost updates
* Stale writes

---

### ✔ Atomic Settlement

All critical trade operations are wrapped in:

```
@Transactional
```

Ensures atomic:

* Balance updates
* Portfolio updates
* Transaction inserts
* Order status updates

---

### ✔ Idempotency Protection

Before execution:

```
if (order.status != PENDING) return;
```

Prevents duplicate scheduler execution.

---

# 🔔 Event-Driven Architecture

Trades publish domain events:

```
TradePlacedEvent
```

Listeners handle:

* Leaderboard updates
* Metrics tracking
* Analytics updates

This provides:

* Loose coupling
* Extensibility
* Post-commit consistency

---

# 📡 Real-Time Layer

* WebSocket (STOMP)
* Live price broadcasting
* Execution feed updates
* JWT-secured WebSocket handshake

---

# 📊 Observability

### ✔ Structured Logging

* Correlation ID per request

### ✔ Micrometer Metrics

* Trade count
* Execution frequency
* Endpoint metrics

### ✔ Actuator Endpoints

* `/actuator/health`
* `/actuator/metrics`
* `/actuator/prometheus`

---

# 🔐 Security Architecture

* JWT Authentication
* Role-based access control (USER / ADMIN)
* Principal-derived user identity
* No userId exposure from frontend
* Secured REST + WebSocket endpoints

---

# 🧩 Persistence Layer

* MySQL
* Hibernate / JPA
* Optimistic locking
* Aggregated queries for leaderboard

---

# 🏗 Deployment Model

* Dockerized Spring Boot container
* Render deployment
* Environment variable configuration
* Stateless design (ready for horizontal scaling)

---

#  System Design Discussion (Interview Section)

This backend models a simplified stock exchange core.

---

## 🔹 How is consistency maintained?

* Optimistic locking prevents concurrent modification
* @Transactional ensures atomic settlement
* Idempotency guards prevent double execution
* Matching and settlement occur inside single transactional boundary

---

## 🔹 What happens if two trades execute simultaneously?

* Version field prevents stale updates
* If conflict occurs → transaction rollback
* Scheduler retry handles consistency

---

## 🔹 How would you scale this system?

1. Shard by stock symbol
2. Use distributed locking (Redis) for scheduler
3. Separate read replicas for analytics
4. Use message broker (Kafka) for event processing
5. Move matching engine into isolated service

---

## 🔹 How would you avoid double execution in distributed setup?

* Distributed lock per symbol
* Database-level row locking
* Leader election mechanism
* Dedicated matching engine node

---

## 🔹 How would you scale to 1M users?

* Stateless application instances
* Load balancer
* Redis for caching
* DB read replicas
* Partitioning by userId or symbol

---

# 🎯 Design Principles Applied

* Separation of Concerns
* Event-Driven Architecture
* Optimistic Concurrency Control
* Atomic Settlement
* Idempotent Processing
* Price-Time Priority Matching

---

# 🚀 System Maturity Level

This backend is no longer a CRUD portfolio app.

It implements:

* Exchange-style order book
* Partial fills
* Transactional trade engine
* Real-time updates
* Concurrency safety
* Observability

---

# 📌 Future Improvements

* Distributed matching engine
* Risk control layer (exposure, circuit breaker)
* Order book depth streaming
* Kafka-based event pipeline
* Horizontal scaling with distributed locks

