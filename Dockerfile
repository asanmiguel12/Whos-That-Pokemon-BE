# ─── Stage 1: build with Gradle ─────────────────────────────────────────────
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy only what’s needed for caching
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle settings.gradle .
COPY --chown=gradle:gradle build.gradle .
COPY --chown=gradle:gradle src src

# Build the fat JAR (skip tests for speed; remove -x test if you want them)
RUN ./gradlew bootJar -x test --no-daemon

# ─── Stage 2: runtime ───────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the bootable JAR from the builder
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# (Optional) expose a default in case you run locally
EXPOSE 8080

# Use Render’s $PORT (falling back to 8080 if not set)
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
# ─── Stage 1: build with Gradle ─────────────────────────────────────────────
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy only what’s needed for caching
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle settings.gradle .
COPY --chown=gradle:gradle build.gradle .
COPY --chown=gradle:gradle src src

# Build the fat JAR (skip tests for speed; remove -x test if you want them)
RUN ./gradlew bootJar -x test --no-daemon

# ─── Stage 2: runtime ───────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the bootable JAR from the builder
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# (Optional) expose a default in case you run locally
EXPOSE 8082

# Use Render’s $PORT (falling back to 8080 if not set)
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8082}"]
