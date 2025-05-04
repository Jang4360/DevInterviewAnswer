# 1단계: 빌드
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test

# 2단계: 실제 실행 환경
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 빌드 단계에서 생성한 JAR 복사
COPY --from=build /app/build/libs/server-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

