# 🏢 Inventory Management System

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]() [![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)]() [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)]()
[![React](https://img.shields.io/badge/React-18.x-blue.svg)]() [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)]()

A complete, production-ready inventory management system for small businesses with multi-warehouse support, supplier management, purchase orders,
sales tracking, low stock alerts, and comprehensive reporting.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Development](#-development)
- [Deployment](#-deployment)
- [Documentation](#-documentation)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Core Functionality

- **Multi-Warehouse Management** - Manage inventory across multiple warehouse locations
- **Product Catalog** - Comprehensive product management with categories and SKUs
- **Purchase Orders** - Create, approve, and track purchase orders with suppliers
- **Sales Orders** - Process sales orders with automated inventory deduction
- **Inventory Transfers** - Transfer stock between warehouses with audit trails
- **Stock Adjustments** - Manual stock adjustments with approval workflows
- **Low Stock Alerts** - Automated notifications for products below reorder levels

### Advanced Features

- **Barcode Integration** - Ready for barcode scanner integration
- **Comprehensive Reporting** - Stock valuation, movement history, sales analysis
- **User Management** - Role-based access control (Admin, Manager, Staff, Viewer)
- **Audit Logging** - Complete audit trail for all critical operations
- **Real-time Dashboard** - Live inventory metrics and analytics
- **File Uploads** - Product images and document attachments (S3 integration)
- **Email Notifications** - Automated alerts for low stock and order updates

## 🛠 Tech Stack

### Backend

- **Framework:** Spring Boot 4.x
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security with JWT
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito, Spring Boot Test

### Frontend

- **Framework:** React 18.x
- **Build Tool:** Vite
- **Language:** TypeScript
- **Routing:** React Router v6
- **State Management:** Zustand / Redux Toolkit
- **UI Components:** Shadcn/UI + Tailwind CSS
- **HTTP Client:** Axios
- **Charts:** Recharts
- **Forms:** React Hook Form + Zod

### DevOps & Infrastructure

- **Containerization:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Cloud:** AWS (EC2, RDS, S3, ALB)
- **IaC:** Terraform
- **Configuration:** Ansible
- **Orchestration:** Kubernetes + Helm
- **Monitoring:** Prometheus + Grafana
- **Logging:** Loki + Promtail
- **Tracing:** Jaeger
- **Web Server:** Nginx
- **Secrets:** HashiCorp Vault

## 🏗 Architecture

```
┌─────────────────┐
│   React SPA     │ (Frontend - Vite + React + TypeScript)
│   Port: 5173    │
└────────┬────────┘
         │
         │ HTTPS
         ▼
┌─────────────────┐
│     Nginx       │ (Reverse Proxy + SSL Termination)
│   Port: 80/443  │
└────────┬────────┘
         │
         │ /api/*
         ▼
┌─────────────────┐
│  Spring Boot    │ (Backend - REST API)
│   Port: 8080    │
└────────┬────────┘
         │
         │ JDBC
         ▼
┌─────────────────┐
│  PostgreSQL     │ (Database)
│   Port: 5432    │
└─────────────────┘
```

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** ([Download](https://adoptium.net/))
- **Node.js 18+** and npm ([Download](https://nodejs.org/))
- **PostgreSQL 15+** ([Download](https://www.postgresql.org/download/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Docker & Docker Compose** (Optional, for containerized development)
- **Git** ([Download](https://git-scm.com/downloads))

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MohamedDev99/inventory-management-system.git
cd inventory-management-system
```

### 2. Database Setup

Create a PostgreSQL database:

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE inventory_db;

# Create user (optional)
CREATE USER inventory_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
```

### 3. Backend Setup

```bash
cd backend

# Configure application properties
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Edit application.yml with your database credentials

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# The backend will start at http://localhost:8080
# API Documentation: http://localhost:8080/swagger-ui.html
```

### 4. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env
# Edit .env with your backend API URL

# Run development server
npm run dev

# The frontend will start at http://localhost:5173
```

### 5. Access the Application

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **API Documentation:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health

### Default Credentials

- **Username:** admin
- **Password:** admin123 (Change immediately in production!)

## 📁 Project Structure

```
inventory-management-system/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/inventory/
│   │   │   │   ├── controller/    # REST controllers
│   │   │   │   ├── service/       # Business logic
│   │   │   │   ├── repository/    # Data access layer
│   │   │   │   ├── entity/        # JPA entities
│   │   │   │   ├── dto/           # Data transfer objects
│   │   │   │   ├── config/        # Configuration classes
│   │   │   │   ├── security/      # Security & JWT
│   │   │   │   └── exception/     # Exception handling
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/  # Flyway migrations
│   │   └── test/                  # Unit & integration tests
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/                   # Vite React frontend
│   ├── src/
│   │   ├── components/        # React components
│   │   │   ├── ui/            # Shadcn UI components
│   │   │   ├── features/      # Feature components
│   │   │   └── layout/        # Layout components
│   │   ├── pages/             # Page components
│   │   ├── services/          # API clients
│   │   ├── hooks/             # Custom React hooks
│   │   ├── utils/             # Utility functions
│   │   ├── types/             # TypeScript types
│   │   ├── store/             # State management
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   └── Dockerfile
│
├── terraform/                  # Infrastructure as Code
│   ├── modules/
│   └── environments/
│
├── ansible/                    # Configuration management
│   ├── playbooks/
│   ├── roles/
│   └── inventory/
│
├── k8s/                       # Kubernetes manifests
│   ├── deployments/
│   ├── services/
│   └── helm-chart/
│
├── .github/
│   └── workflows/             # CI/CD pipelines
│       ├── ci.yml
│       └── deploy.yml
│
├── docker-compose.yml         # Local development
├── docker-compose.dev.yml     # Dev with hot reload
├── .gitignore
├── README.md
└── LICENSE
```

## 💻 Development

### Running with Docker Compose

The easiest way to run the entire stack:

```bash
# Start all services (PostgreSQL, Backend, Frontend, pgAdmin)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (database data)
docker-compose down -v
```

Services will be available at:

- Frontend: http://localhost:80
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432
- pgAdmin: http://localhost:5050

### Running Tests

**Backend:**

```bash
cd backend
mvn test                    # Unit tests
mvn verify                  # Integration tests
mvn test jacoco:report      # Coverage report
```

**Frontend:**

```bash
cd frontend
npm run test               # Unit tests
npm run test:coverage      # Coverage report
npm run lint               # Linting
```

### Database Migrations

This project uses Flyway for database migrations:

```bash
# Migrations are automatically applied on application startup
# Migration files are in: backend/src/main/resources/db/migration/

# Create a new migration
# Name format: V{version}__{description}.sql
# Example: V2__add_supplier_table.sql
```

### Code Quality

```bash
# Backend
cd backend
mvn checkstyle:check       # Code style
mvn spotbugs:check         # Bug detection

# Frontend
cd frontend
npm run lint               # ESLint
npm run format             # Prettier
```

## 🚢 Deployment

### Docker Deployment

```bash
# Build images
docker build -t inventory-backend:latest ./backend
docker build -t inventory-frontend:latest ./frontend

# Tag and push to registry
docker tag inventory-backend:latest MohamedDev99/inventory-backend:v1.0.0
docker push MohamedDev99/inventory-backend:v1.0.0
```

### Cloud Deployment (AWS)

See [DEPLOYMENT.md](docs/DEPLOYMENT.md) for detailed deployment instructions including:

- AWS infrastructure setup
- Terraform provisioning
- Kubernetes deployment
- CI/CD pipeline configuration

### Environment Variables

**Backend:**

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/inventory_db
SPRING_DATASOURCE_USERNAME=inventory_user
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your-secret-key-change-in-production
AWS_ACCESS_KEY_ID=your-aws-key
AWS_SECRET_ACCESS_KEY=your-aws-secret
AWS_S3_BUCKET=your-bucket-name
```

**Frontend:**

```properties
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=Inventory Management System
```

## 📚 Documentation

- [Architecture Documentation](docs/ARCHITECTURE.md)
- [API Documentation](http://localhost:8080/swagger-ui.html)
- [Database Schema](docs/DATABASE_SCHEMA.md)
- [Deployment Guide](docs/DEPLOYMENT.md)
- [Runbook](docs/RUNBOOK.md)
- [Contributing Guide](CONTRIBUTING.md)

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting
pull requests.

### Development Workflow

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'feat: add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks
- `ci:` - CI/CD changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **EL MOUMNY MOHAMED** - _Initial work_ - [MohamedDev99](https://github.com/MohamedDev99)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React and Vite teams for modern frontend tooling
- All open-source contributors whose libraries made this project possible

## 📞 Support

If you have any questions or need help, please:

- Open an [issue](https://github.com/MohamedDev99/inventory-management-system/issues)
- Check the [documentation](docs/)
- Contact: elmoumnymohamed1999[at] gmail [dot] com

## 🗺 Roadmap

- [x] Core inventory management
- [x] Multi-warehouse support
- [x] Purchase and sales orders
- [ ] Mobile application (React Native)
- [ ] Barcode scanner integration
- [ ] Multi-language support
- [ ] Advanced analytics and forecasting
- [ ] Integration with accounting software

---

**Built with ❤️ using Spring Boot, React, and DevOps best practices**
