# -------- 1단계: 빌드 --------
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# 전체 소스 복사
COPY . .

# 테스트 제외하고 빌드 (Render 속도 ↑)
RUN gradle clean bootJar -x test


# -------- 2단계: 실행 --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# 빌드 단계에서 생성된 jar 복사
COPY --from=build /app/build/libs/*.jar app.jar

# Render는 PORT 환경변수를 사용함
ENV PORT=8080

EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
