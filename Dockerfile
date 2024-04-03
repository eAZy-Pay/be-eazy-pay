# 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /usr/src/app

# Gradle 빌드 스크립트 및 관련 파일들을 복사합니다.
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle gradle
COPY src src

# Gradle 빌드를 실행합니다.
RUN ./gradlew build

# App 실행 이미지
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 이전 빌드 스테이지로부터 빌드된 JAR 파일을 가져옵니다.
COPY --from=builder /usr/src/app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
