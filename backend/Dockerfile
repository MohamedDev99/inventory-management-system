# =============================================================================
# STAGE 1: BUILD STAGE
# =============================================================================
# Use the official Maven image with Eclipse Temurin JDK 17.
# This image contains both Maven (build tool) and the JDK (Java compiler).
# We name this stage "builder" so we can reference it later.
# "eclipse-temurin" is the open-source, production-ready JDK from the Eclipse Foundation.
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container.
# All subsequent commands will run from this path.
# If the directory doesn't exist, Docker creates it automatically.
WORKDIR /app

# ── Dependency caching trick ──────────────────────────────────────────────────
# Copy ONLY the pom.xml first (not the source code).
# Docker builds layers; if pom.xml hasn't changed, Docker reuses the cached
# "mvn dependency:go-offline" layer on the next build — saving several minutes.
COPY pom.xml .

# Download all project dependencies into the local Maven cache inside the container.
# "-B" = batch mode (no interactive prompts, cleaner CI output).
# "--no-transfer-progress" suppresses verbose download progress bars.
RUN mvn dependency:go-offline -B --no-transfer-progress

# Now copy the actual source code.
# Placed AFTER the dependency download so Docker only re-downloads deps when
# pom.xml changes, not on every source-code edit.
COPY src ./src

# Package the application into a runnable JAR.
# "-DskipTests" skips unit tests here (tests run separately in the CI pipeline).
# The output JAR lands in /app/target/
RUN mvn package -DskipTests -B --no-transfer-progress


# =============================================================================
# STAGE 2: RUNTIME STAGE
# =============================================================================
# Use a minimal JRE-only image — no compiler, no Maven, no build tools.
# "eclipse-temurin:17-jre-alpine" is ~100 MB vs ~500 MB for the JDK image.
# Alpine Linux is an ultra-small Linux distribution (~5 MB base).
# This is the image that actually gets deployed and run in production.
FROM eclipse-temurin:17-jre-alpine

# ── Security: non-root user ───────────────────────────────────────────────────
# Running as root inside a container is a security risk.
# We create a dedicated system group and user named "spring" with no login shell.
# "-S" = system account, "-G" = assign to group, "-s /bin/false" = no shell login.
RUN addgroup -S spring && adduser -S spring -G spring -s /bin/false

# Set the working directory for the runtime container.
WORKDIR /app

# Copy only the compiled JAR from the builder stage.
# "COPY --from=builder" pulls files from the named "builder" stage.
# We rename it to "app.jar" for a clean, predictable filename at runtime.
# Everything else (source code, Maven cache, build tools) is LEFT BEHIND —
# this is the key security and size benefit of multi-stage builds.
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the JAR to the non-root user we created above.
# "chown spring:spring" ensures the "spring" user can read and execute the file.
RUN chown spring:spring app.jar

# Switch to the non-root user for all subsequent commands and the final process.
USER spring

# Document which port the Spring Boot application listens on.
# EXPOSE is metadata only — it does NOT actually open the port.
# You must map it with "-p 8080:8080" in docker run or docker-compose.
EXPOSE 8080

# ── Health check ──────────────────────────────────────────────────────────────
# Docker will call this command every 30 seconds to verify the container is healthy.
# --interval=30s  → check every 30 seconds
# --timeout=10s   → fail if no response within 10 seconds
# --start-period=60s → give the app 60 seconds to start before health checks begin
# --retries=3     → mark as unhealthy after 3 consecutive failures
# We use the Spring Boot Actuator health endpoint.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# The command to run when the container starts.
# "java -jar app.jar" launches the Spring Boot application.
# ENTRYPOINT is preferred over CMD for the main process — it cannot be
# accidentally overridden by docker-compose command arguments.
ENTRYPOINT ["java", "-jar", "app.jar"]
