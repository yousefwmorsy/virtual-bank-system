# syntax=docker/dockerfile:1
################################################################################
# Stage 1: Resolve and download dependencies (Java 26 JDK)
FROM eclipse-temurin:26-jdk-noble as deps
WORKDIR /build
# Copy the mvnw wrapper with executable permissions.
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
# Download dependencies with cache mounts for speed.
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests
################################################################################
# Stage 2: Package the application (build the JAR)
FROM deps as package
WORKDIR /build
COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar
################################################################################
# Stage 3: Extract Spring Boot layers for optimal Docker caching
FROM package as extract
WORKDIR /build
RUN java -Djarmode=tools -jar target/app.jar extract --layers --destination target/extracted
################################################################################
# Stage 4: Final minimal runtime (Java 26 JRE only)
FROM eclipse-temurin:26-jre-noble AS final
WORKDIR /app
# Create a non-privileged user
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser
# Copy the built JAR directly
COPY --from=package build/target/app.jar ./app.jar
# Expose the application port
EXPOSE 8080
# Run the JAR
ENTRYPOINT [ "java", "-jar", "app.jar" ]