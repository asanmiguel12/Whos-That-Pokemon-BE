FROM node:18-alpine AS base

FROM base AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Copy both package.json and package-lock.json
COPY package*.json ./

RUN npm ci

FROM base AS builder
WORKDIR /app

# Install Java (OpenJDK 17 for Spring Boot compatibility)
RUN apk add --no-cache openjdk17
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

COPY --from=deps /app/node_modules ./node_modules
COPY . .

ENV NEXT_TELEMETRY_DISABLED=1

# Ensure gradlew has execute permissions
RUN chmod +x ./gradlew

RUN ./gradlew build
RUN npm run build

FROM base AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN apk add --no-cache openjdk17
RUN ls /usr/lib/jvm/  # Verify the correct directory
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs
RUN mkdir -p /app/.gradle && chown -R nextjs:nodejs /app/.gradle

# Copy the built files and gradlew script
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/gradlew ./gradlew
COPY --from=builder /app/ ./

USER nextjs

EXPOSE 8082

ENV PORT=8082

CMD ["sh", "./gradlew", "bootRun"]