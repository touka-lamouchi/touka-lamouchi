# ShopHub - Microservices E-Commerce Platform

A modern microservices-based e-commerce platform built with Spring Boot, React, and Docker.

## Project Structure

```
ShopHub/
├── api-gateway/                    # API Gateway Service
├── customer-portal-service/        # Customer Portal Service (GraphQL)
├── order-tracking-service/         # Order Tracking Service (gRPC)
├── orchestrator-service/           # Orchestrator Service
├── payment-service/                # Payment Service (SOAP)
├── product-service/                # Product Service (REST)
├── frontend/                       # React Frontend
├── docker-compose.yml              # Docker Compose Configuration
├── init-db.sql                     # Database Initialization Script
└── README.md                       # This file
```

## Prerequisites

- **Java 11+** (for backend services)
- **Node.js 14+** and npm (for frontend)
- **Docker** and **Docker Compose**
- **Git**
- **Maven** (or use the included mvnw)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

This is the easiest way to run the entire application with all services and database.

#### Steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/touka-lamouchi/touka-lamouchi.git
   cd touka-lamouchi
   ```

2. **Ensure Docker is running**, then start all services:
   ```bash
   docker-compose up -d
   ```

3. **Wait for services to start** (30-60 seconds)

4. **Access the application:**
   - Frontend: `http://localhost:3000`
   - API Gateway: `http://localhost:8080`
   - Payment Service (SOAP): `http://localhost:8082`
   - Product Service: `http://localhost:8083`
   - Order Tracking Service: `http://localhost:8084`
   - Customer Portal Service: `http://localhost:8085`

5. **Stop all services:**
   ```bash
   docker-compose down
   ```

### Option 2: Manual Setup (Local Development)

#### Step 1: Database Setup

1. **Install MySQL** (if not already installed)

2. **Create the database:**
   ```bash
   mysql -u root -p < init-db.sql
   ```
   - This will create the `shophub_db` database with all required tables
   - Default credentials: `root` / `password` (change in production)

3. **Verify the database:**
   ```bash
   mysql -u root -p
   ```
   ```sql
   USE shophub_db;
   SHOW TABLES;
   EXIT;
   ```

#### Step 2: Backend Services Setup

1. **API Gateway (Port 8080):**
   ```bash
   cd api-gateway
   mvn clean install
   mvn spring-boot:run
   ```

2. **Product Service (Port 8083):** (in a new terminal)
   ```bash
   cd product-service
   mvn clean install
   mvn spring-boot:run
   ```

3. **Payment Service (Port 8082):** (in a new terminal)
   ```bash
   cd payment-service
   mvn clean install
   mvn spring-boot:run
   ```

4. **Order Tracking Service (Port 8084):** (in a new terminal)
   ```bash
   cd order-tracking-service
   mvn clean install
   mvn spring-boot:run
   ```

5. **Customer Portal Service (Port 8085):** (in a new terminal)
   ```bash
   cd customer-portal-service
   mvn clean install
   mvn spring-boot:run
   ```

6. **Orchestrator Service (Port 8086):** (in a new terminal)
   ```bash
   cd orchestrator-service
   mvn clean install
   mvn spring-boot:run
   ```

#### Step 3: Frontend Setup

1. **Install dependencies:**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the frontend:**
   ```bash
   npm start
   ```
   - Frontend will open at `http://localhost:3000`

## Database Information

### Database Name
```
shophub_db
```

### Default Credentials
- **Username:** `root`
- **Password:** `password`
- **Host:** `localhost`
- **Port:** `3306`

### Tables Created
- `products` - Product catalog
- `customers` - Customer information
- `orders` - Order management
- `payments` - Payment records
- `tracking` - Order tracking

### Reset Database
To reset the database, run:
```bash
mysql -u root -p -e "DROP DATABASE shophub_db;"
mysql -u root -p < init-db.sql
```

## Services Description

| Service | Port | Protocol | Purpose |
|---------|------|----------|---------|
| API Gateway | 8080 | REST | Entry point for all requests |
| Product Service | 8083 | REST | Product catalog management |
| Payment Service | 8082 | SOAP | Payment processing |
| Order Tracking Service | 8084 | gRPC | Order tracking |
| Customer Portal Service | 8085 | GraphQL | Customer information and orders |
| Orchestrator Service | 8086 | REST | Orchestrates inter-service communication |
| Frontend | 3000 | HTTP | React web application |

## Architecture

```
┌─────────────────────────────────────┐
│      React Frontend (3000)          │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      API Gateway (8080)             │
└────────────┬────────────────────────┘
             │
    ┌────────┴─────────────────────┬──────────┬──────────┐
    │                              │          │          │
┌───▼────────┐  ┌──────────────┐ ┌▼────────┐┌▼─────────┐┌▼──────────┐
│ Product    │  │ Payment      │ │Order    ││Customer ││Orchestrat│
│ Service    │  │ Service      │ │Tracking ││Portal   ││Service   │
│ (8083)     │  │ (8082)       │ │(8084)   ││(8085)   ││(8086)    │
└───┬────────┘  └──────────────┘ └────────┘└─────────┘└──────────┘
    │                              │          │          │
    └────────────────────────────────┬────────┴──────────┘
                                     │
                            ┌────────▼──────────┐
                            │   MySQL Database  │
                            │   (shophub_db)    │
                            └───────────────────┘
```

## Development Tips

### Running Tests
```bash
cd <service-name>
mvn test
```

### Building Docker Images Manually
```bash
docker build -t shophub/<service-name> ./<service-name>
```

### Viewing Logs
```bash
# Using Docker Compose
docker-compose logs -f <service-name>

# Using local services
# Logs appear in the terminal where you ran mvn spring-boot:run
```

### Common Issues

**Port Already in Use:**
```bash
# Find the process using the port (e.g., 8080)
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Mac/Linux

# Kill the process and restart
```

**Database Connection Failed:**
- Ensure MySQL is running
- Check credentials in `application.properties` or `application.yml`
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`

**Docker Image Build Failed:**
```bash
docker-compose up --build  # Force rebuild
```

## API Documentation

### Product Service
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create new product

### Payment Service (SOAP)
- WSDL: `http://localhost:8082/ws/payments.wsdl`

### Order Tracking Service (gRPC)
- Proto file: `order-tracking-service/src/main/proto/order-tracking.proto`

### Customer Portal (GraphQL)
- GraphQL endpoint: `http://localhost:8085/graphql`

## Troubleshooting

### Services won't start
1. Check if ports are already in use
2. Ensure MySQL is running
3. Check logs for specific errors

### Database errors
1. Run `init-db.sql` again
2. Check MySQL user permissions
3. Verify database name in service configurations

### Frontend can't reach backend
1. Ensure all backend services are running
2. Check API Gateway is accessible at `http://localhost:8080`
3. Check CORS configuration in backend

## Contributing

1. Create a feature branch: `git checkout -b feature/feature-name`
2. Commit changes: `git commit -m "Add feature"`
3. Push to remote: `git push origin feature/feature-name`
4. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please create an issue in the GitHub repository.

---

**Last Updated:** December 9, 2025
