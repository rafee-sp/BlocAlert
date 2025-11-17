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

![image](https://private-user-images.githubusercontent.com/195568862/515380100-986a932f-8f3a-4146-9608-5767e2628a53.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTAwLTk4NmE5MzJmLThmM2EtNDE0Ni05NjA4LTU3NjdlMjYyOGE1My5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xMjcxZGE3YmY2YjA0ZDZkOTZmZWY2NjAwNWI4MGNhZjdjMzE2ZTI2YmEyYmU1MTgzOWVjZmVlZDQ0ODYxYjQ3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.mTYx-922uMe1pMuRyDmoW_bDO91ofKIXA9W95OdvVSg)

![image](https://private-user-images.githubusercontent.com/195568862/515380160-ff7c03fd-307c-4194-b5e2-2afc5c112532.gif?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTYwLWZmN2MwM2ZkLTMwN2MtNDE5NC1iNWUyLTJhZmM1YzExMjUzMi5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zOWQ5NjRmMDUzZjA4ZTU3YWIwZDkwYzBhMTE4ZDZiNTRkNjgxZDU4NDAxYTFiZWJjNDlkNWFjZGJiNjEyZjc3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.sR6wgGC0yT-6eumX-wxE-ZwIujt2wKS4U2l3HLsBP7I)

![image](https://private-user-images.githubusercontent.com/195568862/515380458-ee5ff24e-6056-4a15-811b-d3d25ec1c823.gif?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwNDU4LWVlNWZmMjRlLTYwNTYtNGExNS04MTFiLWQzZDI1ZWMxYzgyMy5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02YTY0MDkzMTgyNTNhNmMxM2YzMjM2ZDVmOTIzYTNhOGExOWZlOGI4MmNiY2JiZDFkZWE4MTBlNjA5N2IyZDZiJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.m7iA_hiBVg4ODS8MQZMFwSHwuPfKnmt6auD_-HtF51M)

![image](https://private-user-images.githubusercontent.com/195568862/515380101-dc65b235-2544-41ef-9575-bbbf871b9a02.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTAxLWRjNjViMjM1LTI1NDQtNDFlZi05NTc1LWJiYmY4NzFiOWEwMi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yMjQ4OTI5Y2ExMWI1YzYwMGE5YmE0MzQ5Y2JlZDlkOGY4MGI1NThlM2NiNTc1MTQ0YzA0ZjBhNzYyODkwNjc1JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.965ESrvm9uW4C-xMfKpow-qB4Qd6iT9qwFwnn8meEtw)

![image](https://private-user-images.githubusercontent.com/195568862/515380160-ff7c03fd-307c-4194-b5e2-2afc5c112532.gif?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTYwLWZmN2MwM2ZkLTMwN2MtNDE5NC1iNWUyLTJhZmM1YzExMjUzMi5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zOWQ5NjRmMDUzZjA4ZTU3YWIwZDkwYzBhMTE4ZDZiNTRkNjgxZDU4NDAxYTFiZWJjNDlkNWFjZGJiNjEyZjc3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.sR6wgGC0yT-6eumX-wxE-ZwIujt2wKS4U2l3HLsBP7I)

![image](https://private-user-images.githubusercontent.com/195568862/515380094-ef7f7abc-4762-46d3-aef3-5bed75dce326.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMDk0LWVmN2Y3YWJjLTQ3NjItNDZkMy1hZWYzLTViZWQ3NWRjZTMyNi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00Yzk4ODFkMWRmMzFjMTk0YjAwNDk0YzEyZjRlZjU3ODA2NzVmOTQzMDQyOGIyOTNiZmE3NjU2OTYxMTg3OTQ0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.NB53kOo5CuAtsqYHBqABUaV4qWOwzbJ8lHbHs29ccD8)

![image](https://private-user-images.githubusercontent.com/195568862/515380105-a0bbe61e-69e3-4774-b86a-7af5bdc237c5.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTA1LWEwYmJlNjFlLTY5ZTMtNDc3NC1iODZhLTdhZjViZGMyMzdjNS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1kNDA5N2U0NjBkMzdhNmE1OTk1ZDZiNmFlNTc2NDI0NmNmZjRhNmY1ZTc4MzVhMzZlN2Q0ODljN2ZlNGE3NjBjJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.nGN7aXi6DFAAdfeDB1i14gdyNLlJMC7y4rnIPOfBOLM)

![image](https://private-user-images.githubusercontent.com/195568862/515380097-b4be87d2-9314-46ad-a94a-cdf4c80cb157.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMDk3LWI0YmU4N2QyLTkzMTQtNDZhZC1hOTRhLWNkZjRjODBjYjE1Ny5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zMzNiYTY5ZGM4NDRkMTliOTZmODE0NWZlNWQ2NDcyZWEzZTZjNGI2NjhjZWZmOTViNTY5NzI4NDJmZWZjYWE2JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.rTSAsOnd5o_44P-btimZDELslf54_ko2h-128lKDMwM)

![image](https://private-user-images.githubusercontent.com/195568862/515380096-afeaf66a-ed65-4afb-acb4-35e7b9ab1f89.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMDk2LWFmZWFmNjZhLWVkNjUtNGFmYi1hY2I0LTM1ZTdiOWFiMWY4OS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1jYWJiYzIyMTJlMTJlZDFlMmVkMGU2NjZkMjk1MTE5M2JmODczMWNiNWYyNmMzMjU4NGNiNjAyMjU0NDg2MzgxJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.I-wKGFnsxFtydv0HplbAtNCZAJ7KUfTgdPvARMaChkk)

![image](https://private-user-images.githubusercontent.com/195568862/515380102-866c2bf0-508c-4d0b-a122-bb5aa77babe3.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTAyLTg2NmMyYmYwLTUwOGMtNGQwYi1hMTIyLWJiNWFhNzdiYWJlMy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iZmI0ODM0YWE1OWJkZjkyZmY4N2EyYzAzNzA0NDZjOGZmMDkwNTM1ZGI3MjlkMzZiZGVhNTI4NWYyMzZkN2Y0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.iYhXB9v0c9gfq5MPkyZaW2gXIbd5uIcQAgwUFOn7Imo)

![image](https://private-user-images.githubusercontent.com/195568862/515380099-73a1f548-07a4-4c6e-a1d7-3d0a9f279a70.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMDk5LTczYTFmNTQ4LTA3YTQtNGM2ZS1hMWQ3LTNkMGE5ZjI3OWE3MC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02ZmI4MjU5NmJlMjJhZTM2ZWJiOGJjMzAxNDAzYmExYzRhNzNlODhiYWQwYzJhMDc1NTg2NDg1Y2RmYjlhNWQyJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.3HIQCk4e6T1ccU2IPIw9jDLuM6AqKc25L-zLGza7qdw)

![image](https://private-user-images.githubusercontent.com/195568862/515380103-b3d11e55-f36d-494d-8331-01d29ecf0302.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMTAzLWIzZDExZTU1LWYzNmQtNDk0ZC04MzMxLTAxZDI5ZWNmMDMwMi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05MmU1N2VkMzcwNjk1YzNlM2JjZDE1ZGQ1MTkwZTU3NDM1ZTZlMTk2M2JmZGFkMDk3ZjcyYjAwNjM5ZGM3MzU0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.8rZFbqwupbpFWh381n14LGiNDQJnSsfA5ypzbw8sgME)

![image](https://private-user-images.githubusercontent.com/195568862/515380098-ed75b792-3b0e-47b8-99a2-06f243eb496b.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTE5NDEsIm5iZiI6MTc2MzQxMTY0MSwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgwMDk4LWVkNzViNzkyLTNiMGUtNDdiOC05OWEyLTA2ZjI0M2ViNDk2Yi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDM0MDFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1kZjI1MzVhYmMyMWJhNTQ4NzFkOGE2MDVmZWJiY2JhY2E2Zjc2Y2FkMGExODE5NTJhZjJmZGFiYzYyN2MwNmRkJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.VRQOr-EKKYmFjNCbaWJZ6Om30SuKbMVQohb23J-p7hc)

![image](https://private-user-images.githubusercontent.com/195568862/515383156-f0f2f445-e604-4a03-97c6-06f674e307a1.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTMwMzYsIm5iZiI6MTc2MzQxMjczNiwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1MzgzMTU2LWYwZjJmNDQ1LWU2MDQtNGEwMy05N2M2LTA2ZjY3NGUzMDdhMS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDUyMTZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iMTBkNmMwZDAxYTQ1Yzk5NmUzOTAzOGJjOGZmY2NjYzEwOTBiMGRjMjRlMGViNzk1MTFhNzEwMzVhMWNhYmE2JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.C1F7heCeoRm4RQ2mjvGQtOMg_Yd6Uw5RXOxJh3C7paU)

![image](https://private-user-images.githubusercontent.com/195568862/515384471-3d7f8218-7bc1-4a6d-9cb2-b09ed94848db.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTMwMzYsIm5iZiI6MTc2MzQxMjczNiwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg0NDcxLTNkN2Y4MjE4LTdiYzEtNGE2ZC05Y2IyLWIwOWVkOTQ4NDhkYi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDUyMTZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05ZTNlOTc5YTU5YmY2N2ZmNzJkMjE0NzhiNjhkZWI5NWVhMzNkYmE2NmUyYzFjOTYwODdhOTM3NTcxNjU1NDFmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.Bm1N16-8apg6lHjbkuXwubp2rOScQSI7gc-okcm_CIA)

![image](https://private-user-images.githubusercontent.com/195568862/515386761-61d2ac5e-09ed-4182-8b71-f614f8a14e57.jpg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM0MTMwMzYsIm5iZiI6MTc2MzQxMjczNiwicGF0aCI6Ii8xOTU1Njg4NjIvNTE1Mzg2NzYxLTYxZDJhYzVlLTA5ZWQtNDE4Mi04YjcxLWY2MTRmOGExNGU1Ny5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTE3JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTExN1QyMDUyMTZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1kODMxMDFjN2E2OWVmNjg3MGRkNTg1YTQ5YTJiNWQzNDk5NTM1ZWQwYzhjNzZmZjE5Y2MxOTA5NmM3YzI2YTU4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.bv367qas0bwF6FHIdu9oHoEZyVXu-b8yEK4jXZBjamE)

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
