# ============================================
# Dockerfile for GreenhouseAdmin (Kotlin Multiplatform - WASM)
# Multi-stage build for optimized production image
# ============================================

# Stage 1: Build the WASM application
FROM gradle:8.10-jdk21 AS builder

# CRÍTICO: Instalar libatomic1 para Node.js v25+ (requerido por Kotlin/WASM)
# Ver: https://github.com/nodejs/node/issues/issues - Node.js v25 requiere libatomic
USER root
RUN apt-get update && apt-get install -y --no-install-recommends \
    libatomic1 \
    && rm -rf /var/lib/apt/lists/*
USER gradle

WORKDIR /app

# Configurar memoria para JVM/Gradle
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m"

# Copy Gradle configuration files first (for better caching)
COPY --chown=gradle:gradle gradle/ gradle/
COPY --chown=gradle:gradle gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./

# Copy the compose app module
COPY --chown=gradle:gradle composeApp/ composeApp/

# Make gradlew executable
RUN chmod +x gradlew

# Build the WASM distribution
RUN ./gradlew :composeApp:wasmJsBrowserDistribution --no-daemon --stacktrace

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Install required packages for WASM MIME types
RUN apk add --no-cache tzdata

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the built WASM application
COPY --from=builder /app/composeApp/build/dist/wasmJs/productionExecutable/ /usr/share/nginx/html/

# Create a simple health check endpoint
RUN echo "OK" > /usr/share/nginx/html/health

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
