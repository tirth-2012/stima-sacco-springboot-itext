# =========================
# Stage 1: Build with Maven
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom first (cache optimization)
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Copy source
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jdk-jammy

# Create non-root user
RUN useradd -ms /bin/bash appuser

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set permissions
RUN chown appuser:appuser /app/app.jar

# Switch user
USER appuser

# Expose port
EXPOSE 8098

# JVM Optimization
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --spider -q http://localhost:8098/actuator/health || exit 1

# Start app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]