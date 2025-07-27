# Halo Game Platform - Complete Deployment Guide
**Date:** July 26, 2025  
**Author:** jguida941  
**Version:** 1.0.0

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Backend Deployment](#backend-deployment)
4. [Unity Client Setup](#unity-client-setup)
5. [Database Configuration](#database-configuration)
6. [Production Deployment](#production-deployment)
7. [Docker Deployment](#docker-deployment)
8. [Monitoring Setup](#monitoring-setup)
9. [Troubleshooting](#troubleshooting)
10. [Performance Tuning](#performance-tuning)

## Prerequisites

### System Requirements
- **Operating System:** macOS 10.15+, Windows 10+, or Ubuntu 18.04+
- **Java:** OpenJDK 8 or higher
- **Maven:** 3.6.0 or higher
- **Unity:** 2021.3 LTS or higher
- **Memory:** Minimum 8GB RAM, Recommended 16GB+
- **Storage:** 5GB free space for development environment

### Development Tools
```bash
# Check Java version
java -version
# Should output: openjdk version "1.8.0_xxx" or higher

# Check Maven version  
mvn --version
# Should output: Apache Maven 3.6.x or higher

# Install Unity Hub (if not already installed)
# Download from: https://unity.com/download
```

## Local Development Setup

### Step 1: Clone and Prepare Backend
```bash
# Navigate to the project directory
cd "/Users/jguida941/Desktop/untitled folder 2/java-rest-api"

# Build the project
mvn clean compile

# Run tests to verify setup
mvn test

# Package the application
mvn package

# Verify JAR creation
ls -la target/gameauth-1.0-SNAPSHOT.jar
```

### Step 2: Configuration Setup
```bash
# Review configuration file
cat config.yml

# The default configuration includes:
# - Server port: 8080
# - CORS enabled for localhost
# - Basic authentication
# - JSON logging format
```

### Step 3: Start Backend Server
```bash
# Method 1: Using Maven
mvn exec:java -Dexec.mainClass="com.gamingroom.gameauth.GameAuthApplication" -Dexec.args="server config.yml"

# Method 2: Using JAR directly
java -jar target/gameauth-1.0-SNAPSHOT.jar server config.yml

# Method 3: Using provided script
./run-server.sh

# Method 4: Using Python launcher (with UI)
python3 run_server.py
```

### Step 4: Verify Backend Deployment
```bash
# Test health check
curl http://localhost:8080/healthcheck

# Test authentication
curl -u admin:admin http://localhost:8080/gameusers

# Test Halo endpoints
curl -u admin:admin http://localhost:8080/halo/player/985752863/stats

# Expected response: JSON with player statistics
```

## Backend Deployment

### Production Configuration

#### config-production.yml
```yaml
# Production configuration
server:
  type: simple
  applicationContextPath: /
  adminContextPath: /admin
  connector:
    type: http
    port: 8080
    bindHost: 0.0.0.0

# Logging configuration
logging:
  level: INFO
  loggers:
    com.gamingroom.gameauth: DEBUG
  appenders:
    - type: console
      threshold: INFO
      target: stdout
    - type: file
      threshold: INFO
      currentLogFilename: /var/log/gameauth/application.log
      archivedLogFilenamePattern: /var/log/gameauth/application-%d{yyyy-MM-dd}.log.gz
      archivedFileCount: 30

# Database configuration (for production PostgreSQL)
database:
  driverClass: org.postgresql.Driver
  url: ${DATABASE_URL:-jdbc:postgresql://localhost:5432/halo_game}
  user: ${DATABASE_USER:-gameauth}
  password: ${DATABASE_PASSWORD:-secure_password}
  properties:
    charSet: UTF-8
    hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
    hibernate.hbm2ddl.auto: validate
    
# Connection pooling
  maxWaitForConnection: 1s
  validationQuery: SELECT 1
  validationQueryTimeout: 3s
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: false
  evictionInterval: 10s
  minIdleTime: 1m

# Metrics and monitoring
metrics:
  frequency: 1m
  reporters:
    - type: console
      timeZone: UTC
      output: stdout
    - type: graphite
      host: localhost
      port: 2003
      prefix: gameauth
      
# Health checks
health:
  healthCheckUrlPaths: ["/health-check"]
  healthChecks:
    - name: "database"
      critical: true
    - name: "deadlocks"
      critical: true
```

### Systemd Service Configuration

#### /etc/systemd/system/gameauth.service
```ini
[Unit]
Description=GameAuth Halo Platform
After=network.target

[Service]
Type=simple
User=gameauth
Group=gameauth
WorkingDirectory=/opt/gameauth
ExecStart=/usr/bin/java -Xms1g -Xmx2g -jar /opt/gameauth/gameauth.jar server /opt/gameauth/config-production.yml
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=gameauth

# Environment variables
Environment=DATABASE_URL=jdbc:postgresql://localhost:5432/halo_game
Environment=DATABASE_USER=gameauth
Environment=DATABASE_PASSWORD=secure_password

# Security settings
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/var/log/gameauth

[Install]
WantedBy=multi-user.target
```

### Deployment Steps
```bash
# 1. Create deployment user
sudo useradd -r -s /bin/false gameauth

# 2. Create directories
sudo mkdir -p /opt/gameauth
sudo mkdir -p /var/log/gameauth
sudo chown gameauth:gameauth /opt/gameauth /var/log/gameauth

# 3. Copy application files
sudo cp target/gameauth-1.0-SNAPSHOT.jar /opt/gameauth/gameauth.jar
sudo cp config-production.yml /opt/gameauth/
sudo chown gameauth:gameauth /opt/gameauth/*

# 4. Install and start service
sudo cp gameauth.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable gameauth
sudo systemctl start gameauth

# 5. Verify deployment
sudo systemctl status gameauth
curl http://localhost:8080/healthcheck
```

## Unity Client Setup

### Development Environment

#### Prerequisites
```bash
# Install Unity Hub
# Download from: https://unity.com/download

# Install Unity 2021.3 LTS
# Through Unity Hub: Installs > Add > Unity 2021.3 LTS
```

#### Project Setup
```bash
# 1. Open Unity Hub
# 2. Click "Open" 
# 3. Navigate to: unity-halo-client/
# 4. Unity will import project (first time takes 5-10 minutes)

# Alternative: Command line (if Unity CLI tools installed)
/Applications/Unity/Hub/Editor/2021.3.x/Unity.app/Contents/MacOS/Unity -projectPath "unity-halo-client"
```

### Build Configuration

#### Build Settings
```csharp
// File: ProjectSettings/ProjectSettings.asset
PlayerSettings.companyName = "jguida941";
PlayerSettings.productName = "Halo Game Platform";
PlayerSettings.applicationIdentifier = "com.jguida941.halogame";
PlayerSettings.bundleVersion = "1.0.0";

// Platform-specific settings
#if UNITY_STANDALONE_WIN
    PlayerSettings.defaultScreenWidth = 1920;
    PlayerSettings.defaultScreenHeight = 1080;
    PlayerSettings.fullScreenMode = FullScreenMode.FullScreenWindow;
#elif UNITY_STANDALONE_OSX
    PlayerSettings.defaultScreenWidth = 1920;
    PlayerSettings.defaultScreenHeight = 1080;
    PlayerSettings.macRetinaSupport = true;
#endif
```

#### Quality Settings
```csharp
// File: ProjectSettings/QualitySettings.asset
// High Quality Preset:
QualitySettings.antiAliasing = 4;
QualitySettings.shadowResolution = ShadowResolution.High;
QualitySettings.shadowDistance = 150.0f;
QualitySettings.vSyncCount = 1;
```

### Build Process
```bash
# Unity Command Line Build (automated)
unity -batchmode -quit -projectPath "unity-halo-client" \
      -buildTarget StandaloneOSX \
      -buildPath "Builds/HaloGame.app" \
      -executeMethod BuildScript.BuildGame

# Or use Unity Editor:
# 1. File > Build Settings
# 2. Select target platform
# 3. Click "Build"
# 4. Choose output directory
```

## Database Configuration

### PostgreSQL Setup (Production)

#### Installation
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# macOS (using Homebrew)
brew install postgresql
brew services start postgresql

# Windows (using installer from postgresql.org)
# Download and run installer
```

#### Database Creation
```sql
-- Connect as postgres user
sudo -u postgres psql

-- Create database and user
CREATE DATABASE halo_game;
CREATE USER gameauth WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE halo_game TO gameauth;

-- Connect to halo_game database
\c halo_game

-- Run schema creation
\i /path/to/halo_schema.sql

-- Verify tables
\dt

-- Expected output:
-- player_stats, custom_maps, match_results, etc.
```

#### Connection Testing
```bash
# Test connection
psql -h localhost -U gameauth -d halo_game

# Run test query
SELECT COUNT(*) FROM player_stats;
```

### Redis Setup (Caching)

#### Installation
```bash
# Ubuntu/Debian
sudo apt install redis-server

# macOS
brew install redis
brew services start redis

# Windows
# Download from: https://github.com/microsoftarchive/redis/releases
```

#### Configuration
```bash
# Edit redis.conf
sudo nano /etc/redis/redis.conf

# Key settings:
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

## Production Deployment

### AWS Deployment

#### Infrastructure Setup
```bash
# Create VPC and security groups
aws ec2 create-vpc --cidr-block 10.0.0.0/16
aws ec2 create-security-group --group-name gameauth-sg --description "GameAuth Security Group"

# Allow HTTP traffic
aws ec2 authorize-security-group-ingress \
    --group-name gameauth-sg \
    --protocol tcp \
    --port 8080 \
    --cidr 0.0.0.0/0

# Allow SSH access
aws ec2 authorize-security-group-ingress \
    --group-name gameauth-sg \
    --protocol tcp \
    --port 22 \
    --cidr 0.0.0.0/0
```

#### EC2 Instance Launch
```bash
# Launch EC2 instance
aws ec2 run-instances \
    --image-id ami-0abcdef1234567890 \
    --count 1 \
    --instance-type t3.medium \
    --key-name my-key-pair \
    --security-groups gameauth-sg \
    --user-data file://user-data.sh
```

#### user-data.sh
```bash
#!/bin/bash
yum update -y
yum install -y java-1.8.0-openjdk postgresql

# Create gameauth user
useradd -r gameauth

# Download and setup application
mkdir -p /opt/gameauth
cd /opt/gameauth
wget https://github.com/jguida941/halo_game0726/releases/download/v1.0.0/gameauth.jar
wget https://raw.githubusercontent.com/jguida941/halo_game0726/main/config-production.yml

# Setup systemd service
wget https://raw.githubusercontent.com/jguida941/halo_game0726/main/gameauth.service -O /etc/systemd/system/gameauth.service

# Start service
systemctl daemon-reload
systemctl enable gameauth
systemctl start gameauth
```

### Load Balancer Configuration

#### Application Load Balancer
```json
{
  "LoadBalancerName": "gameauth-alb",
  "Scheme": "internet-facing",
  "Type": "application",
  "IpAddressType": "ipv4",
  "SecurityGroups": ["sg-gameauth"],
  "Subnets": ["subnet-12345", "subnet-67890"],
  "Tags": [
    {
      "Key": "Name",
      "Value": "GameAuth Load Balancer"
    }
  ]
}
```

#### Target Group Configuration
```json
{
  "Name": "gameauth-targets",
  "Protocol": "HTTP",
  "Port": 8080,
  "VpcId": "vpc-12345",
  "HealthCheckProtocol": "HTTP",
  "HealthCheckPath": "/healthcheck",
  "HealthCheckIntervalSeconds": 30,
  "HealthCheckTimeoutSeconds": 5,
  "HealthyThresholdCount": 2,
  "UnhealthyThresholdCount": 5
}
```

## Docker Deployment

### Dockerfile
```dockerfile
# Multi-stage build for Java backend
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:8-jre-alpine

# Install curl for health checks
RUN apk --no-cache add curl

# Create non-root user
RUN addgroup -g 1001 gameauth && \
    adduser -D -s /bin/sh -u 1001 -G gameauth gameauth

WORKDIR /app

# Copy application and config
COPY --from=builder /app/target/gameauth-1.0-SNAPSHOT.jar app.jar
COPY config.yml .

# Change ownership
RUN chown -R gameauth:gameauth /app

# Switch to non-root user
USER gameauth

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/healthcheck || exit 1

# Run application
CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "app.jar", "server", "config.yml"]
```

### Docker Compose
```yaml
# docker-compose.yml
version: '3.8'

services:
  gameauth:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=jdbc:postgresql://postgres:5432/halo_game
      - DATABASE_USER=gameauth
      - DATABASE_PASSWORD=secure_password
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - gameauth-network

  postgres:
    image: postgres:13-alpine
    environment:
      - POSTGRES_DB=halo_game
      - POSTGRES_USER=gameauth
      - POSTGRES_PASSWORD=secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./src/main/resources/halo_schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U gameauth"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - gameauth-network

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
    networks:
      - gameauth-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - gameauth
    networks:
      - gameauth-network

volumes:
  postgres_data:
  redis_data:

networks:
  gameauth-network:
    driver: bridge
```

### Deployment Commands
```bash
# Build and run
docker-compose up --build -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f gameauth

# Scale application
docker-compose up -d --scale gameauth=3

# Update deployment
docker-compose pull
docker-compose up -d
```

## Monitoring Setup

### Prometheus Configuration
```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "gameauth_rules.yml"

scrape_configs:
  - job_name: 'gameauth'
    static_configs:
      - targets: ['localhost:8081']  # Metrics endpoint
    metrics_path: /metrics
    scrape_interval: 5s

  - job_name: 'postgres'
    static_configs:
      - targets: ['localhost:9187']  # postgres_exporter

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093
```

### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "GameAuth Halo Platform",
    "tags": ["gameauth", "halo", "gaming"],
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{status}}"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, http_request_duration_seconds_bucket)",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Active Players",
        "type": "stat",
        "targets": [
          {
            "expr": "gameauth_active_players",
            "legendFormat": "Players Online"
          }
        ]
      }
    ]
  }
}
```

### Log Analysis with ELK Stack

#### Logstash Configuration
```ruby
# logstash.conf
input {
  beats {
    port => 5044
  }
}

filter {
  if [fields][service] == "gameauth" {
    json {
      source => "message"
    }
    
    date {
      match => [ "timestamp", "yyyy-MM-dd HH:mm:ss,SSS" ]
    }
    
    if [logger] == "com.gamingroom.gameauth.halo.controller.HaloGameResource" {
      mutate {
        add_tag => ["halo_api"]
      }
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "gameauth-%{+YYYY.MM.dd}"
  }
}
```

## Troubleshooting

### Common Issues

#### Backend Won't Start
```bash
# Check Java version
java -version
# Must be Java 8 or higher

# Check port availability
netstat -tulpn | grep :8080
# Port should be free

# Check application logs
tail -f server.log

# Common solutions:
# 1. Kill process using port 8080
sudo lsof -i :8080
sudo kill -9 <PID>

# 2. Check config.yml syntax
yaml-lint config.yml

# 3. Verify JAR integrity
java -jar target/gameauth-1.0-SNAPSHOT.jar --version
```

#### Unity Build Failures
```bash
# Clear Unity cache
rm -rf Library/
rm -rf Temp/

# Reimport assets
# Unity Editor: Assets > Reimport All

# Check Unity console for errors
# Common issues:
# 1. Missing script references
# 2. Platform-specific compilation errors
# 3. Missing dependencies
```

#### Database Connection Issues
```bash
# Test PostgreSQL connection
psql -h localhost -U gameauth -d halo_game

# Check PostgreSQL status
sudo systemctl status postgresql

# Reset database
sudo -u postgres psql -c "DROP DATABASE halo_game;"
sudo -u postgres psql -c "CREATE DATABASE halo_game;"
```

#### Performance Issues
```bash
# Check system resources
top
htop
free -h

# Check application metrics
curl http://localhost:8081/metrics

# Profile Java application
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=app.jfr -jar app.jar

# Analyze with JProfiler or VisualVM
```

### Debug Mode Setup
```bash
# Enable debug logging
java -Ddw.logging.level=DEBUG -jar gameauth.jar server config.yml

# Remote debugging
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar gameauth.jar server config.yml

# Connect debugger to port 5005
```

## Performance Tuning

### JVM Tuning
```bash
# Production JVM settings
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+UseStringDeduplication \
     -XX:+PrintGC \
     -XX:+PrintGCDetails \
     -XX:+PrintGCTimeStamps \
     -Xloggc:gc.log \
     -jar gameauth.jar server config-production.yml
```

### Database Optimization
```sql
-- Create indexes for better performance
CREATE INDEX idx_player_stats_gamertag ON player_stats(gamertag);
CREATE INDEX idx_player_stats_rank_level ON player_stats(rank_level);
CREATE INDEX idx_custom_maps_author ON custom_maps(author_id);
CREATE INDEX idx_custom_maps_game_mode ON custom_maps(game_mode);

-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM player_stats ORDER BY total_kills DESC LIMIT 100;

-- Update table statistics
ANALYZE player_stats;
ANALYZE custom_maps;
```

### Unity Performance
```csharp
// Object pooling for UI elements
public class UIElementPool : MonoBehaviour {
    private Queue<GameObject> pool = new Queue<GameObject>();
    
    public GameObject GetElement() {
        if (pool.Count > 0) {
            return pool.Dequeue();
        }
        return Instantiate(prefab);
    }
    
    public void ReturnElement(GameObject element) {
        element.SetActive(false);
        pool.Enqueue(element);
    }
}

// Efficient texture loading
TextureImporter importer = (TextureImporter)AssetImporter.GetAtPath(assetPath);
importer.textureCompression = TextureImporterCompression.Compressed;
importer.maxTextureSize = 1024;
importer.SaveAndReimport();
```

### Network Optimization
```java
// Connection pooling for HTTP clients
ConnectionPoolManager.setPoolSize(50);
ConnectionPoolManager.setConnectionTimeout(30000);
ConnectionPoolManager.setReadTimeout(60000);

// GZIP compression for responses
@GZIP
@GET
@Path("/leaderboard/{stat}")
public Response getLeaderboard(@PathParam("stat") String stat) {
    // Returns compressed response
}
```

---

**Last Updated:** July 26, 2025  
**Version:** 1.0.0  
**Deployment Status:** Production Ready