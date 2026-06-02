# NLSTechFlowableSpringbootService

Enterprise Loan Management System built using:

* Java Spring Boot
* Flowable BPM
* ReactJS
* PostgreSQL
* Redis
* NGINX
* Mayan EDMS
* Docker & Docker Compose

---

# Table of Contents

* [Production Users](#production-users)
* [System Architecture](#system-architecture)
* [Prerequisites](#prerequisites)
* [Development Environment Setup](#development-environment-setup)
* [Pre-Production / Production Deployment](#pre-production--production-deployment)
* [Client VM Docker Setup](#client-vm-docker-setup)
* [Mayan EDMS Setup](#mayan-edms-setup)
* [Useful Docker Commands](#useful-docker-commands)
* [Application URLs](#application-urls)
* [Swagger API](#swagger-api)
* [Troubleshooting](#troubleshooting)

---

# Production Users

| Username    | Password   | Role                             | Name                | Email                                                     | Description                    |
| ----------- | ---------- | -------------------------------- | ------------------- | --------------------------------------------------------- | ------------------------------ |
| `sacca.admin` | `DIB@2026` | `SYSTEM_ADMINISTRATOR`   | Admin       | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | System Administrator 
| `pwanjiku`   | `DIB@2026` | `BRANCH_MANAGER`   | Patricia Wanjiku        | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)     | Branch Manager, Eligibility
| `gkristan`   | `DIB@2026` | `GUARANTOR_VERIFICARION_OFFICER` | Gary Christan | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Guarantor Verification Officer
| `akapoor`   | `DIB@2026` | `CREDIT_APRAISAL` | Ayesha Kapoor | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)     | Credit Apraisal
| `jmwangi`   | `DIB@2026` | `CREDIT_APRAISAL` | James Mwangi  | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)     | Credit Apraisal
| `sndungu`   | `DIB@2026` | `BRANCH_MANAGER`    | Samuel Ndung'u | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)  | Branch Manager
| `kmutua`    | `DIB@2026` | `CREDIT_OFFICER`  | Kevin Mutua  | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)  | Credit Officer 
| `fochieng`  | `DIB@2026` | `SENIOR_CREDIT_MANAGER` | Fatuma Ochieng | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Senior Credit Manager 
| `pomondi`   | `DIB@2026` | `SENIOR_CREDIT_MANAGER` | Paul Omondi  | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Senior Credit Manager
| `rnjeri`    | `DIB@2026` | `CREDIT_COMMITTEE` | Ruth Njeri | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)  | Credit Committee
| `ckimani`   | `DIB@2026` | `CREDIT_COMMITTEE` | Charles Kimani | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Credit Committee
| `jakinyi`   | `DIB@2026` | `CREDIT_COMMITTEE` | Joyce Akinyi | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Credit Committee
| `hali`      | `DIB@2026` | `BRANCH_CREDIT_COMMITTEE` | Hassan Ali | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Branch Credit Committee 
| `mokeyo`    | `DIB@2026` | `LEGAL_OFFICER` | Michael Okeyo | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Legal Officer, Documentation
| `gkamau`    | `DIB@2026` | `LEGAL_OFFICER` | Grace Kamau | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com) | Legal Officer, Documentation
| `sochieng`  | `DIB@2026` | `CREDIT_ADMINISTRATOR` | Sandra Ochieng | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Disbursement 
| `pkariuki`  | `DIB@2026` | `CREDIT_ADMINISTRATOR` | Peter Kariuki  | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Disbursement
| `STM-00038111` | `DIB@2026` | `CUSTOMER`  | Faith Nyambura Wairimu | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer
| `STM-00038112` | `DIB@2026` | `CUSTOMER`  | Samuel Mutua Nzioka | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer
| `STM-00038113` | `DIB@2026` | `CUSTOMER`  | Mary Akinyi Onyango | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer
| `STM-00038114` | `DIB@2026` | `CUSTOMER`  | Brian Otieno Wafula | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer
| `STM-00038115` | `DIB@2026` | `CUSTOMER`  | Joseph Mwangi Kariuki | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer
| `STM-00038116` | `DIB@2026` | `CUSTOMER`  | Catherine Wanja Njoki | [sg.vadaviya@gmail.com](mailto:sg.vadaviya@gmail.com)   | Customer


---

# System Architecture

```text
ReactJS UI
     |
NGINX Reverse Proxy
     |
------------------------------------------------
|                     |                        |
Spring Boot API    Flowable BPM           Mayan EDMS
     |
PostgreSQL + Redis
```

---

# Prerequisites

Install the following software before setup:

* Docker
* Docker Compose
* Git
* Java 17
* Maven
* Node.js (for React UI development)

---

# Development Environment Setup

## 1. Start Docker Services

```bash
docker compose up
```

---

## 2. Create PostgreSQL Database

```sql
CREATE DATABASE flowable;
```

---

## 3. Start Tomcat

```bash
catalina.bat run
```

---

## 4. Access Mayan EDMS

URL:

```text
http://localhost:8069/#/authentication/login/?next=/home/%3F/
```

Fetch generated Mayan admin password from logs.

Update the password in:

```yaml
application.yaml
```

---

## 5. Run Spring Boot Application

Run the Spring Boot application from IDE or terminal.

---

## 6. Upload Product Document Checklist

```powershell
.\upload_product_document_checklist.ps1
```

---

## 7. Create Task Checklists

```powershell
.\create-tasks-checklists.ps1
```

---

# Pre-Production / Production Deployment

## Start Full Stack

```bash
docker compose --file=docker-compose-flowable-springboot.yml up
```

---

## Rebuild Containers

```bash
docker compose --file=docker-compose-flowable-springboot.yml up --build
```

---

# Client VM Docker Setup

## 1. Generate SSH Key

```bash
ssh-keygen -t ed25519 -C "your-email@example.com"
```

---

## 2. Connect to VM

```bash
ssh sumit@102.37.105.250
```

---

## 3. Install Docker

```bash
sudo apt update
sudo apt install docker.io docker-compose -y
```

---

## 4. Enable Docker

```bash
sudo systemctl enable docker
sudo systemctl start docker
```

---

## 5. Clone Repositories

### Backend Repository

```bash
git clone https://github.com/rutu-soft/NLSTechFlowableSpringbootService.git
```

### React UI Repository

```bash
git clone https://github.com/rutu-soft/NLSTechReactUI.git
```

---

## 6. Navigate to Project

```bash
cd NLSTechFlowableSpringbootService
```

---

## 7. Stop Existing Containers

```bash
sudo docker stop $(sudo docker ps -q)
```

---

## 8. Remove Existing Deployment

```bash
sudo docker-compose -f docker-compose-flowable-springboot.yml down -v
```

---

## 9. Remove Docker Images

```bash
sudo docker rmi -f $(sudo docker images -q)
```

---

## 10. Cleanup Docker System

```bash
sudo docker system prune -a --volumes -f
```

---

## 11. Start Application Stack

```bash
sudo docker-compose -f docker-compose-flowable-springboot.yml up
```

---

# Mayan EDMS Setup

## Generate Random Secret

```bash
openssl rand -base64 32
```

---

## Disable Auto OCR

Run query:

```sql
SELECT * FROM public.ocr_documenttypeocrsettings;
```

Disable OCR settings as required.

---

## Obtain API Token

```bash
curl --location 'https://localhost/mayan/api/v4/auth/token/obtain/' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{"username":"admin","password":"YOUR_PASSWORD"}'
```

---

# Application URLs

## React UI

```text
https://localhost/
```

Production:

```text
http://102.37.105.250/
```

---

## Mayan EDMS

```text
https://nlsbanking.com/mayan/authentication/login/?next=/mayan/home/%3F/
```

---

## Flowable UI

```text
https://nlsbanking.com/flowable/
```

---

# Swagger API

## Spring Boot Swagger

```text
http://102.37.105.250/flowable-service/swagger-ui/index.html
```

---

# Useful Docker Commands

## List Running Containers

```bash
docker ps
```

---

## List Docker Images

```bash
docker images
```

---

## Remove Specific Image

```bash
sudo docker rmi nlstechflowablespringbootservice_react-ui:latest
```

---

## Stop All Containers

```bash
sudo docker stop $(sudo docker ps -q)
```

---

# Troubleshooting

## Read-Only File in Vim

Error:

```text
E505: file is read-only
```

Save forcefully:

```bash
:w!
```

---

## Docker Cleanup

Remove all unused containers, images, volumes, and networks:

```bash
sudo docker system prune -a --volumes -f
```

---

# Notes

* Ensure Docker daemon is running before starting containers.
* Update Mayan admin password in `application.yaml`.
* Use HTTPS in production environments.
* Configure proper reverse proxy and SSL certificates for production deployment.

---

# License

Internal Project - NLSTech / Rutusoft IT Services LLP
