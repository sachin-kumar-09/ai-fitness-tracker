# 🏋️ AI Fitness Tracker

An AI-powered fitness tracking platform built using **Spring Boot Microservices**, **Apache Kafka**, **MongoDB**, **PostgreSQL**, **Eureka Service Discovery**, and **Google Gemini AI**.

The platform tracks fitness activities, processes events asynchronously using Kafka, and generates personalized AI-powered workout recommendations.

---

# 🚀 Features

- 👤 User Management Service
- 🏃 Activity Tracking
- 🤖 AI-based Fitness Recommendations
- 📩 Kafka Event Streaming
- 🔍 Eureka Service Discovery
- 🍃 MongoDB for activity & recommendations
- 🐘 PostgreSQL for user management
- ⚡ Gemini AI integration for personalized fitness coaching

---

# 🏗️ Microservice Architecture

```text
                   +------------------+
                   | Eureka Server    |
                   | Service Registry |
                   +--------+---------+
                            |
         ---------------------------------------------
         |                     |                     |
         |                     |                     |
+--------v------+   +---------v------+   +---------v------+
| User Service  |   | ActivityTracker|   | AI Service     |
| PostgreSQL    |   | MongoDB        |   | MongoDB        |
+---------------+   +--------+-------+   +--------+-------+
                             |
                             |
                      Kafka Producer
                             |
                      activity-events
                             |
                      Kafka Consumer
                             |
                       Gemini AI API
                             |
                     Recommendation Saved
