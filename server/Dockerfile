# 1. JDK 17 기반 이미지 사용
FROM openjdk:17

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 4. 8080 포트 열기 (Spring Boot 기본 포트)
EXPOSE 8080

# 5. JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
