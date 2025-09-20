# ─── Stage 1: Build with Gradle ─────────────────────────────────────────────
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy Gradle wrapper and build files first (for dependency caching)
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle settings.gradle .
COPY --chown=gradle:gradle build.gradle .

# Download dependencies (cached unless build.gradle/settings.gradle change)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || return 0

# Now copy the source code
COPY --chown=gradle:gradle src src

# Build the fat JAR (skip tests for speed; remove -x test if you want them)
RUN ./gradlew bootJar -x test --no-daemon --stacktrace --info -Dorg.gradle.console=plain

# ─── Stage 2: Runtime ───────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the bootable JAR from the builder
COPY --from=builder /home/gradle/project/build/libs/*SNAPSHOT.jar app.jar

# Expose default port (Render overrides with $PORT)
EXPOSE 8080

# Use Render’s $PORT (falling back to 8080 locally)
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
