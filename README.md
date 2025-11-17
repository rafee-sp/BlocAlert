# BlocAlert

A modern, production‑grade **crypto alerting platform** where users can:

* View **top 250 cryptocurrencies** by market cap (via CoinGecko API)
* See **real‑time price updates** via WebSockets
* Create **price alerts** for: in‑app notifications, SMS, and email
* Get **5 free alerts**, with premium features unlocked via **Stripe subscription**

BlocAlert is built with a scalable microservice‑friendly architecture, production‑ready observability, secure authentication, and clean developer experience.

---

## Features

### Crypto Data & Real‑Time Updates

* Fetches **top 250 coins** from CoinGecko
* Real‑time price stream via **WebSocket**
* Smooth UI updates with React + Vite + Tailwind

### Alerts System

* Create alerts based on price targets
* Notification channels:

  * **In‑app notification** (WebSocket push)
  * **Email** (AWS SES)
  * **SMS** (Twilio)
* **Free tier limits:** 5 alerts per user
* **Premium tier:** unlimited in‑app alerts + SMS + Email via **Stripe subscription**

### Authentication & Authorization

* **Auth0** handles user identity on both frontend & backend
* Backend enforces JWT & scopes for:

  * Alert management
  * Subscription features
  * User profile

### Production‑Grade Architecture

* Java **21** + Spring Boot
* Redis caching
* Kafka event pipeline
* WebSocket event streaming
* MySQL persistent database
* Centralized logging & monitoring using:

  * **Sentry** (frontend)
  * **Loki + Prometheus + Grafana + Actuator** (backend metrics)
* Rate limiting with **Guava RateLimiter**

---

## Tech Stack

### **Frontend**

* React (Vite)
* Tailwind CSS
* Axios
* Auth0 SPA SDK
* Sentry for error tracking
* WebSocket client for real‑time updates

### **Backend**

* Java 21
* Spring Boot (Web, Security, Data JPA, WebSocket)
* Auth0 JWT authentication
* Redis (cache + throttling)
* Kafka (trigger + streaming events)
* Guava Rate Limiter
* MySQL
* Stripe SDK (subscription payments)
* AWS SES (email)
* Twilio (SMS)

### **DevOps / Observability**

* Docker / Docker Compose
* Prometheus (metrics)
* Grafana dashboard
* Loki log aggregation
* Actuator health endpoints
* Sentry (frontend error logs)

---

## Application screenshots

![image](https://private-user-images.githubusercontent.com/195568862/515388032-d203c6f2-1993-4772-9306-e91415079b1a.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDMyLWQyMDNjNmYyLTE5OTMtNDc3Mi05MzA2LWU5MTQxNTA3OWIxYS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04ZDg2MmM1M2NkYTQ3OTk3M2Y5YzZiMzUyY2UyZDdhYTlkNjY3NmNkZjM0YzU4OTAyOGU5NjQ3YTU0MmY5OTEyJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.o1XCu-cSHNrvzzRs4jzeeCq-iHonPYFkQowgNeNM2fM)

![image](https://private-user-images.githubusercontent.com/195568862/515388141-8435eea8-8b3e-4262-bc34-c1ce0c5828d0.gif?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MTQxLTg0MzVlZWE4LThiM2UtNDI2Mi1iYzM0LWMxY2UwYzU4MjhkMC5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1hOTAxMDIwYTMyZTEzNDU4YmQ0YzUxZGQwM2NmZGU3MWViZjgwMjkyOGUyZjA4YzhkODNlOTE1M2ExMzdhYjRlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.J5HUEgRfI60uOaOBj5jNGqumtJVHdL2TZKXehczhlhI)

![image](https://private-user-images.githubusercontent.com/195568862/515388142-0a571d98-eb15-4e28-a15f-6e5dbfe3fc7a.gif?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MTQyLTBhNTcxZDk4LWViMTUtNGUyOC1hMTVmLTZlNWRiZmUzZmM3YS5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yOWNhOGQ1MTUwZjJhOWQxZDNmNTJiOGQyNjc0ZjUxNWY4MDVkY2I4NWI4OGM4NjU0ZTdhMzgzM2YzN2E1ZWFmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.abzxLLJrwLqM99BgqyKgVg_pvbrKfAtG6synIeaVGmU)

![image](https://private-user-images.githubusercontent.com/195568862/515388035-5325db5f-48e0-4502-9290-caf77a25a0a8.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM1LTUzMjVkYjVmLTQ4ZTAtNDUwMi05MjkwLWNhZjc3YTI1YTBhOC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1mNTU0OThkNmE4ZTM2MDNmMjdhMmYzYTY4ZWVjYWY2ODE4NTgwNmM1ZDE0OWIxYWEyN2Q2ZTE5ZDc1YTA2YTE3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9._lVmipvus-61Z4NGAOMgekiSZJrQwBh85dEy4kpCPtw)

![image](https://private-user-images.githubusercontent.com/195568862/515388036-96e83f3b-17f7-443d-a9de-34d186e996aa.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM2LTk2ZTgzZjNiLTE3ZjctNDQzZC1hOWRlLTM0ZDE4NmU5OTZhYS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xMDA4YTQyN2RmOTJlMmJkOTYwMDk4MzA5YjgzZjM0MmZjM2IzNTA0MjM2MmVhMmZiZTFmMGQ5Mjg2ODczYjQ4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.X_q82-r9W16TOqoBi_narelMJOK7OGq3VZubFod7Q0k)

![image](https://private-user-images.githubusercontent.com/195568862/515388034-4411c21d-f780-4155-aff9-a70c14f57a49.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM0LTQ0MTFjMjFkLWY3ODAtNDE1NS1hZmY5LWE3MGMxNGY1N2E0OS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02OTcwN2UyOGU0Mjk4OTM5MjYzOTdkMWQ4MTQ5ZTFmOTJmOTU1ZTFiN2NhNDI3NjhiMDczMGUyMmYyYjI4MzZjJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.KIGH2EEc8guN8XgPEfYNwqgSqqNnNvl8FifftGIRyf8)

![image](https://private-user-images.githubusercontent.com/195568862/515388033-5f2410db-970d-4de7-8508-7b798e0acc97.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDMzLTVmMjQxMGRiLTk3MGQtNGRlNy04NTA4LTdiNzk4ZTBhY2M5Ny5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xZGVhZDg3N2VjMDhiZWNjMGU3ZGJkYjE5YWY5YTIyMDMwMThiNTllN2RiMTA2YTNhYzc1MGI1Y2MxMzJjNjlhJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.eQIZCb_X2VUnppVnhBk0hxuxwdRosn0i4tNsvfaSuw8)

![image](https://private-user-images.githubusercontent.com/195568862/515388038-1816f772-f400-490d-8771-e8e9a9fcfc17.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM4LTE4MTZmNzcyLWY0MDAtNDkwZC04NzcxLWU4ZTlhOWZjZmMxNy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1hZWFjNWUxMzdhMWE3NWJiZjIxNzk3OGFiODFlNjgyNzc0MzFiODI2NjY4YzMzMzM3MTk4YmNmOTdhNmJkM2UyJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.dJHZo5sUgwYhdQpo5ykfOzDJqR_26MACJCqaCVTyGXA)

![image](https://private-user-images.githubusercontent.com/195568862/515388040-ea5eb9bc-b6d1-484b-9818-725d43134ebc.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDQwLWVhNWViOWJjLWI2ZDEtNDg0Yi05ODE4LTcyNWQ0MzEzNGViYy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04MTllYTViNjkwMzI4MWU1YTNmYTQ1NzlkNDQyZjBkYTY3Mjk1MWZjMzI1Nzg5ZmM1ZWNhYzA1NDkwZjE5Mjg0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.eTxKI4MvVA9EmEzsEsCBnd-JKp4_1wvczDSCWHM0Cjw)

![image](https://private-user-images.githubusercontent.com/195568862/515388042-0e67c8c5-f8e9-427a-94f6-e471678718d2.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDQyLTBlNjdjOGM1LWY4ZTktNDI3YS05NGY2LWU0NzE2Nzg3MThkMi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1jZmVhNzQ4MGI2YzAxYmYzOGQxMzQ4NDIyMGU1Mzc5MWY5NzIyNzE2MWUxOTIyODZiYmU3MmQwNTk5OWFhMzJlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.kWQZiz_4tZH3STJvwCQ069PwnKVKqhmOMcCik60tS9M)

![image](https://private-user-images.githubusercontent.com/195568862/515388037-8f3652d6-d79e-4189-a2db-18443a1196e3.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM3LThmMzY1MmQ2LWQ3OWUtNDE4OS1hMmRiLTE4NDQzYTExOTZlMy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xMmFjNDQ3NGNjNTM5ZWU1NjI1MDE2MDY4MTlmODQ5MGRhMDdmZTBiNzQxZGYxYjMxNmRiMjY1MDNiNGNjOWQ5JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.04OYAElon-auBCTuPrFlqsmTgvXHvDAMJHhjou63Qrw)

![image](https://private-user-images.githubusercontent.com/195568862/515388041-bcbd5c6a-0732-4b15-8892-fa580ccd3e97.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDQxLWJjYmQ1YzZhLTA3MzItNGIxNS04ODkyLWZhNTgwY2NkM2U5Ny5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1lOGI2MDhkNGZiZDlhNDFmOTI5YWQxOWE0MmU1NWQwZmM4YzYzNzYxOGRmODkzYWU3NjJjOTZkNTk1OWM0MjY4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.3iVaTcyKrac__0F1qiZGgIa8M3J6-cgNrOFdF8Vin60)

![image](https://private-user-images.githubusercontent.com/195568862/515388039-5993f652-cc93-43d3-878d-bd3b78493c61.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4MDM5LTU5OTNmNjUyLWNjOTMtNDNkMy04NzhkLWJkM2I3ODQ5M2M2MS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zZTVhOTRmYWMwNjExMTMxYTljNTI0ZjQxOTQ5YmZiZDU0MzhkMzMwYmQyODNlOTg3ZDA1NTA5MWNlNWJjYWE1JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.C_qUqoyTzl2QRZHzaWYoWatSU1VkjNHYxS3fCtlmJZk)

![image](https://private-user-images.githubusercontent.com/195568862/515388624-641dbe31-ecc3-45dc-919c-16994cd9d72a.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4NjI0LTY0MWRiZTMxLWVjYzMtNDVkYy05MTljLTE2OTk0Y2Q5ZDcyYS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xOTIwN2ZmNTI2YmJiNWM0NWJhZWIyMmUwN2E2MDQ0MTEwYjU0ZDBmNzIxYTBhYjFjNDVlZGM2N2UxZGViNjU3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.i7lWN4w497-ygqOzscjoDOy_1woYo4Vv7Ho-ysYUayE)

![image](https://private-user-images.githubusercontent.com/195568862/515388950-974ddaeb-482a-4c59-a0b3-ce9d7bee9544.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4OTUwLTk3NGRkYWViLTQ4MmEtNGM1OS1hMGIzLWNlOWQ3YmVlOTU0NC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05ZDdkYzk5ZWM0YTEwMTdlN2RmMDFhOWY1NDM5OTM4ZGI5NjBhYThmODI5OTAxZDkzMzQxMDc3YjkyYzViMTM0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.lRfTOQ5LnxT64BSZCdKX7PfJPym5uuyyphu-04xMyag)

![image](https://private-user-images.githubusercontent.com/195568862/515388520-7bf2dd38-a2e3-43ad-9572-79931a606f16.jpg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTM0MjAsIm5iZiI6MTc2MzQxMzEyMCwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg4NTIwLTdiZjJkZDM4LWEyZTMtNDNhZC05NTcyLTc5OTMxYTYwNmYxNi5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDU4NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00NjhjMmI1MGZiM2JiNjFlNjhiNjEyNDQ2NGRjYmE2ZDk2MmNjMjUxNzRjZDY5MTEzNDVhZjRjYjc3NTgxMzAxJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.1md7xC5Qy6vN9ssQ7fsUgrqN5ZOSs3xJF6ikCtOdLeY)


## System Workflow (High‑Level)


### 1. **User Interface**

* User logs in via Auth0
* Frontend fetches top 250 coins
* WebSocket connection opens for live price updates

### 2. **Alert Creation Flow**

* User sets a price trigger 
* Alert is saved in MySQL and Redis
* Event sent to Kafka

### 3. **Price Monitoring Service**

* Background worker fetches live prices
* When threshold met → alert event is emitted
* Notification service picks up the event

### 4. **Notification Pipeline**

Depending on user plan:

* Free tier → in‑app alert only
* Premium -> in‑app + email + SMS 

### 5. **Real‑Time Updates**

* Backend broadcasts alerts using WebSocket
* UI receives alert instantly

### 6. **Subscription Flow (Stripe)**

* User chooses a premium plan
* Stripe Checkout session is created
* Webhook updates user role in Auth0 + DB

---

## 📁 Project Structure

```
BlocAlert/
├── backend/               # Spring Boot backend
│   ├── src/main/java
│   ├── src/main/resources
│   
└── frontend/              # React + Vite app
    ├── src/
    ├── public/    
```

---

## 🔧 Environment Variables

### **Backend (.env or application.yml)**

### **Frontend (.env)**

```
VITE_BACKEND_URL=http://localhost:8080/api/v1
VITE_AUTH0_DOMAIN=
VITE_AUTH0_CLIENT_ID=
VITE_AUTH0_AUDIENCE=
VITE_SENTRY_DSN=
VITE_WS_URL=ws://localhost:8080/ws
```

---

## ▶️ Local Development

### **1. Clone Repo**

```
git clone https://github.com/rafee-sp/BlocAlert.git
cd BlocAlert
```

### **2. Run Backend**

```
cd backend
./mvnw spring-boot:run
```

### **3. Run Frontend**

```
cd frontend
npm install
npm run dev
```

Frontend: [http://localhost:5173](http://localhost:5173)
Backend: [http://localhost:8080](http://localhost:8080)

---
