# 📈 Portfolio Management System

A solo practice project built to master the fundamentals of **Spring Boot 3/4**, **JPA/Hibernate**, and **Dockerized Development**. This application manages complex investment portfolio hierarchies and validates financial business rules.

---

## 🚀 Key Features

* **Recursive Hierarchy:** Supports nested portfolios (Parent-Child relationships).
* **Safety Guards:** Custom logic to prevent circular references (a portfolio cannot be its own ancestor).
* **Depth Control:** Strict business rule to limit portfolio nesting to a maximum of 5 levels.
* **DTO Pattern:** Clean separation between Database Entities and API Data Transfer Objects.
* **Containerized DB:** Ready-to-use MySQL environment via Docker Compose.

---

## 🛠️ Tech Stack

* **Framework:** Java 21 & Spring Boot
* **Persistence:** Spring Data JPA (Hibernate)
* **Database:** MySQL 9.6
* **Build & Dependency:** Maven
* **DevOps:** Docker & Docker Compose
* **Testing:** JUnit 5 & AssertJ

---

## ⚙️ Setup & Installation

### 1. Prerequisites
* **JDK 21** installed.
* **Docker Desktop** running.
* An IDE (IntelliJ IDEA preferred).

### 2. Environment Configuration
Create a `.env` file in the project root (this file is ignored by Git). Add your local credentials:

```properties
MYSQL_ROOT_PASSWORD=your_secret_password
MYSQL_DATABASE=portfolio
DB_USERNAME=root
DB_PASSWORD=your_secret_password