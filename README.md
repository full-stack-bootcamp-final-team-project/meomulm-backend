# 머묾 (Meomulm) - 백엔드 서버

> 숙박 예약 플랫폼을 위한 Spring Boot 기반 RESTful API 서버

## 목차

- [**프로젝트 소개**](#프로젝트-소개)
- [**팀원 소개**](#팀원-소개)
- [**주요 기능**](#주요-기능)
- [**기술 스택**](#기술-스택)
- [**시작하기**](#시작하기)
- [**프로젝트 구조**](#프로젝트-구조)
- [**API 문서**](#api-문서)
- [**환경 설정**](#환경-설정)
- [**개발 가이드**](#개발-가이드)
- [**빌드 및 배포**](#빌드-및-배포)

## 프로젝트 소개

머묾 백엔드는 숙박 예약 플랫폼의 모든 비즈니스 로직과 데이터 처리를 담당하는 서버 애플리케이션으로 Spring Boot 3.2와 MyBatis를 활용하여 안정적이고 확장 가능한 RESTful API를 제공한다.

### 주요 특징

- **JWT 기반 인증** - Stateless 인증 방식으로 확장성 확보
- **PostgreSQL 데이터베이스** - 안정적인 관계형 데이터베이스 사용
- **Stripe 결제 연동** - 안전한 온라인 결제 처리
- **WebSocket 실시간 통신** - STOMP 프로토콜 기반 채팅 시스템
- **MyBatis ORM** - 유연한 SQL 관리 및 데이터 매핑
- **AOP 로깅** - 체계적인 요청/응답 로깅
- **스케줄링** - 예약 상태 자동 관리 및 알림 발송

## 팀원 소개

### Backend Team (6명)

| 이름  | 역할                                                            | 담당 기능                                                        | GitHub                                        |
|:----|:--------------------------------------------------------------|:-------------------------------------------------------------|:----------------------------------------------|
| 유기태 | Backend Lead & Review Feature Owner                           | 아키텍처 설계, 공통 모듈 구축, DB 스키마 설계 및 최적화, 배포·인프라 구성, 리뷰·평점 시스템 API | [tiradovi](https://github.com/tiradovi)       |
| 박형빈 | Backend Developer (Auth Feature Owner)                        | 사용자 인증·인가 API, JWT 토큰 관리, 카카오 소셜 로그인 연동                      | [PHB-1994](https://github.com/PHB-1994)       |
| 조연희 | Backend Developer (Accommodation Feature Owner)               | 숙박시설 관리 API, 검색·필터링 로직, 숙박시설 상세정보 API, 이미지 관리                | [yeonhee-cho](https://github.com/yeonhee-cho) |
| 박세원 | Backend Developer (Product·Reservation·Payment Feature Owner) | 객실(상품) 관리 API, 예약 시스템 API, Stripe 결제 연동, 예약 상태 관리            | [svv0003](https://github.com/svv0003)         |
| 현윤선 | Backend Developer (Favorite Feature Owner)                    | 찜하기 기능 API, 관련 데이터 관리 및 연동                                   | [yunseonhyun](https://github.com/yunseonhyun) |
| 오유성 | Backend Developer (User Feature Owner)                        | 사용자 정보 관리 API, 프로필 관리, 사용자 관련 비즈니스 로직                        | [Emma10003](https://github.com/Emma10003)     |

### 개발 기간

- 2025.12.17 ~ 2026.02.12 (8주)

### 기술적 기여

#### 아키텍처 설계

- **Layered Architecture**: Controller → Service → Mapper 계층 분리
- **MyBatis 기반 영속성**: XML 매퍼를 통한 유연한 쿼리 관리
- **DTO 패턴**: Request/Response 객체 분리로 데이터 캡슐화

#### 기술적 도전과제

- **동시성 제어**: 예약 중복 방지를 위한 낙관적/비관적 락 구현
- **트랜잭션 관리**: 결제-예약 간 원자성 보장
- **성능 최적화**: N+1 쿼리 문제 해결, 커넥션 풀 튜닝
- **보안**: SQL Injection 방지, XSS 필터링, CORS 설정

#### 주요 성과

- RESTful API 설계 원칙 준수
- JWT 기반 Stateless 인증으로 서버 확장성 확보
- AOP를 활용한 관점 지향 프로그래밍 (로깅, 트랜잭션)
- WebSocket을 통한 실시간 양방향 통신 구현
- Scheduled Task를 통한 자동화된 비즈니스 로직 처리

## 주요 기능

### 1. 사용자 인증(`/api/auth`)

- 회원가입/로그인 (이메일/비밀번호)
- 소셜 로그인 (카카오)
- JWT 토큰 발급 및 갱신
- 비밀번호 찾기/변경
- 회원 탈퇴

### 2. 사용자 관리 (`/api/users`)

- 회원 정보 조회/수정
- 회원 예약 내용 조회
- 회원 리뷰 내용 조회

### 3. 숙박시설 관리 (`/api/accommodation`)

- 숙박시설 목록 조회 (검색, 필터링)
- 숙박시설 상세 정보 조회
- 지역별/카테고리별 조회
- 숙박시설 이미지 관리
- 시설 정보 및 편의시설 조회

### 4. 상품(객실) 관리 (`/api/product`)

- 객실 목록 조회
- 객실 상세 정보
- 가격 및 할인 정보
- 재고 관리
- 예약 가능 여부 확인

### 5. 예약 관리 (`/api/reservation`)

- 예약 생성
- 예약 목록 조회 (예정/완료/취소)
- 예약 상세 조회
- 예약 변경/취소
- 예약 상태 자동 관리 (스케줄링)

### 6. 결제 처리 (`/api/payment`)

- Stripe Payment Intent 생성
- 결제 성공/실패 처리
- 결제 내역 조회
- 환불 처리

### 7. 리뷰 시스템 (`/api/review`)

- 리뷰 작성/수정/삭제
- 숙박시설별 리뷰 조회
- 사용자 리뷰 조회

### 8. 찜하기 (`/api/favorite`)

- 찜 추가/삭제
- 찜 목록 조회
- 찜 여부 확인

### 9. 알림 시스템 (`/api/notification`)

- WebSocket 연결
- 알림 목록 조회
- 알림 읽음 처리
- 예약 관련 자동 알림

### 10. 챗봇 (`/api/chat`)

- 채팅방 생성
- 메시지 전송/수신
- 채팅 내역 조회
- api 호출 및 규칙기반 시스템 호출

## 기술 스택

### Backend Framework

- **Spring Boot** 3.2.1 - Java 기반 애플리케이션 프레임워크
- **Java** 21 - 프로그래밍 언어

### Database & ORM

- **PostgreSQL** - 관계형 데이터베이스
- **MyBatis** 3.0.4 - SQL 매퍼 프레임워크
- **HikariCP** - 커넥션 풀

### Security

- **Spring Security** - 인증/인가 프레임워크
- **JWT (jjwt)** 0.12.5 - JSON Web Token 구현

### Communication

- **Spring Web** - RESTful API
- **Spring WebSocket** - 실시간 양방향 통신
- **STOMP** - 메시징 프로토콜

### Payment

- **Stripe Java SDK** 31.3.0 - 결제 처리

### Utilities

- **Lombok** - 보일러플레이트 코드 감소
- **Spring Validation** - 요청 데이터 검증
- **Spring AOP** - 관점 지향 프로그래밍
- **Spring Dotenv** 4.0.0 - 환경변수 관리

### DevOps & Tools

- **Spring Boot DevTools** - 개발 편의 도구
- **Logback** - 로깅 프레임워크
- **Gradle** - 빌드 도구
- **JUnit** - 단위 테스트

## 시작하기

### 필수 요구사항

- Java JDK 21 이상
- Gradle 7.x 이상
- PostgreSQL 12 이상
- IDE (IntelliJ IDEA 권장)

### 설치 및 실행

1. **저장소 클론**

```bash
git clone https://github.com/your-repo/meomulm-backend.git
cd meomulm-backend
```

2. **환경변수 설정**
   프로젝트 루트에 `.env` 파일 생성:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/meomulm
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key_minimum_256_bits
JWT_EXPIRE_MS=86400000

# Kakao OAuth
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_REDIRECT_URL=http://localhost:8080/api/user/kakao/callback

# Stripe
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key

# Gemini AI (선택사항)
GEMINI_API_KEY=your_gemini_api_key
```

3. **데이터베이스 설정**

```bash
# PostgreSQL 접속
psql -U postgres

# 데이터베이스 생성
CREATE DATABASE meomulm;

# 스키마 및 테이블 생성 (DDL 스크립트 실행)
# src/main/resources/schema.sql 참조
```

4. **의존성 설치 및 빌드**

```bash
# Gradle 의존성 다운로드
./gradlew clean build

# 테스트 제외 빌드
./gradlew clean build -x test
```

5. **애플리케이션 실행**

```bash
# 개발 모드
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar
```

6. **서버 확인**

```bash
curl http://localhost:8080/health
```

## 프로젝트 구조

```
meomulm-backend/
├── gradle/                         # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/com/meomulm/
│   │   │   ├── MeomulmBackendApplication.java  # 메인 애플리케이션
│   │   │   ├── accommodation/      # 숙박시설 모듈
│   │   │   ├── chat/               # 채봇 모듈
│   │   │   ├── common/             # 공통 모듈
│   │   │   │   ├── aop/            # AOP (로깅)
│   │   │   │   ├── config/         # 설정 (Security, DB, WebSocket)
│   │   │   │   ├── controller/     # 공통 컨트롤러
│   │   │   │   ├── exception/      # 예외 처리
│   │   │   │   ├── scheduling/     # 스케줄 작업
│   │   │   │   └── util/           # 유틸리티 (JWT, 파일)
│   │   │   ├── favorite/           # 찜하기 모듈
│   │   │   ├── notification/       # 알림 모듈
│   │   │   ├── product/            # 상품(객실) 모듈
│   │   │   ├── payment/            # 결제 모듈
│   │   │   ├── reservation/        # 예약 모듈
│   │   │   ├── review/             # 리뷰 모듈
│   │   │   └── user/               # 사용자 모듈
│   │   └── resources/
│   │       ├── application.properties   # 기본 설정
│   │       ├── config.properties        # 환경변수 매핑
│   │       ├── logback-spring.xml       # 로깅 설정
│   │       ├── mybatis-config.xml       # MyBatis 설정
│   │       └── mappers/                 # MyBatis XML 매퍼
│   │           ├── accommodationMapper.xml
│   │           ├── chatKnowMapper.xml
│   │           ├── chatMapper.xml
│   │           ├── favoriteMapper.xml
│   │           ├── notificationMapper.xml
│   │           ├── productMapper.xml
│   │           ├── paymentMapper.xml
│   │           ├── reservationMapper.xml
│   │           ├── reviewMapper.xml
│   │           └── userMapper.xml
│   └── test/                       # 테스트 코드
├── logs/                           # 로그 파일
├── build.gradle                    # Gradle 빌드 설정
├── settings.gradle                 # Gradle 프로젝트 설정
└── README.md                       # 프로젝트 문서
```

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST API 엔드포인트
│  (@RestController, @RequestMapping) │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│          Service Layer              │  ← 비즈니스 로직
│      (@Service, @Transactional)     │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│          Mapper Layer               │  ← 데이터 접근
│      (@Mapper, MyBatis XML)         │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         PostgreSQL Database         │  ← 데이터 저장소
└─────────────────────────────────────┘
```

## API 문서

### 기본 정보

- **Base URL**: `http://localhost:8080`
- **API Version**: v1
- **Content-Type**: `application/json`
- **인증**: JWT Bearer Token

### 주요 엔드포인트

#### 인증 (Authentication)

```http
POST   /api/user/signup          # 회원가입
POST   /api/user/login           # 로그인
POST   /api/user/refresh         # 토큰 갱신
POST   /api/user/find-id         # 아이디 찾기
POST   /api/user/reset-password  # 비밀번호 재설정
GET    /api/user/kakao/callback  # 카카오 로그인 콜백
```

#### 사용자 (User)

```http
GET    /api/users/profile         # 프로필 조회
GET    /api/users/reservation     # 회원 예약 내역 조회
PUT    /api/users/userInfo        # 회원 정보 수정
PATCH  /api/users/profileImage    # 프로필 사진 수정
POST   /api/users/currentPassword # 현재 비밀번호 확인
PATCH  /api/users/password        # 비밀번호 수정
DELETE /api/users                 # 회원 탈퇴
```

#### 숙박시설 (Accommodation)

```http
GET    /api/accommodation              # 숙박시설 목록 조회
GET    /api/accommodation/{id}         # 숙박시설 상세 조회
GET    /api/accommodation/search       # 검색 (쿼리 파라미터)
GET    /api/accommodation/{id}/images  # 이미지 목록
```

#### 상품 (Product)

```http
GET    /api/product/{accommodationId}  # 객실 목록 조회
GET    /api/product/detail/{id}        # 객실 상세 조회
GET    /api/product/availability       # 예약 가능 여부 확인
```

#### 예약 (Reservation)

```http
POST   /api/reservation                # 예약 생성
GET    /api/reservation                # 예약 목록 조회
GET    /api/reservation/{id}           # 예약 상세 조회
PUT    /api/reservation/{id}           # 예약 변경
DELETE /api/reservation/{id}           # 예약 취소
```

#### 결제 (Payment)

```http
POST   /api/payment/create-intent      # Payment Intent 생성
POST   /api/payment/confirm            # 결제 확인
POST   /api/payment/refund             # 환불 처리
GET    /api/payment/history            # 결제 내역
```

#### 리뷰 (Review)

```http
POST   /api/review                     # 리뷰 작성
GET    /api/review/{accommodationId}  # 숙박시설 리뷰 조회
PUT    /api/review/{id}                # 리뷰 수정
DELETE /api/review/{id}                # 리뷰 삭제
GET    /api/review/my                  # 내 리뷰 조회
```

#### 찜하기 (Favorite)

```http
POST   /api/favorite                   # 찜 추가
DELETE /api/favorite/{accommodationId} # 찜 삭제
GET    /api/favorite                   # 찜 목록 조회
GET    /api/favorite/check/{id}        # 찜 여부 확인
```

#### 알림 (Notification)

```http
GET    /api/notification               # 알림 목록 조회
PUT    /api/notification/{id}/read     # 알림 읽음 처리
DELETE /api/notification/{id}          # 알림 삭제
```

#### 챗봇

```
POST    /api/chat                                # 메세지 전송
GET     /api/chat/conversations/{conversationId} # 대화방 기록 조회
GET     /api/chat/conversations                  # 모든 대화방 조회 
```

### 인증 헤더 예시

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 응답 형식

```json
{
  "success": true,
  "message": "Success",
  "data": {
    
  }
}
```

### 에러 응답

```json
{
  "success": false,
  "message": "Error message",
  "error": "ERROR_CODE"
}
```

## 환경 설정

### 개발 환경

- **포트**: 8080
- **프로파일**: development
- **로그 레벨**: DEBUG
- **데이터베이스**: 로컬 PostgreSQL

### 프로덕션 환경

- **포트**: 8080 (리버스 프록시 뒤)
- **프로파일**: production
- **로그 레벨**: INFO
- **데이터베이스**: RDS PostgreSQL

### application.properties

```properties
spring.application.name=meomulm-backend
server.port=8080
server.address=0.0.0.0
```

### config.properties (환경변수 매핑)

```properties
# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
# HikariCP
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000
# MyBatis
mybatis.mapper-locations=classpath:/mappers/**/*.xml
# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRE_MS}
# File Upload
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=200MB
# Stripe
stripe.secret.key=${STRIPE_SECRET_KEY}
```

## 개발 가이드

### 코드 컨벤션

- Java 코드 스타일: Google Java Style Guide 기반
- 네이밍: camelCase (메서드, 변수), PascalCase (클래스)
- 패키지 구조: 기능별 모듈화

### 새 API 추가하기

1. **DTO 작성** (`model/dto/`)

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExampleRequest {
    @NotNull(message = "필수 값입니다")
    private String name;

    @Min(value = 0, message = "0 이상이어야 합니다")
    private Integer value;
}
```

2. **Mapper 인터페이스** (`model/mapper/`)

```java

@Mapper
public interface ExampleMapper {
    List<Example> selectAll();

    Example selectById(Long id);

    int insert(Example example);

    int update(Example example);

    int delete(Long id);
}
```

3. **MyBatis XML** (`resources/mappers/`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.meomulm.example.model.mapper.ExampleMapper">

    <select id="selectAll" resultType="Example">
        SELECT * FROM examples
    </select>

    <select id="selectById" resultType="Example">
        SELECT * FROM examples WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO examples (name, value)
        VALUES (#{name}, #{value})
    </insert>
</mapper>
```

4. **Service 작성** (`model/service/`)

```java

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleServiceImpl implements ExampleService {

    private final ExampleMapper exampleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Example> getAllExamples() {
        return exampleMapper.selectAll();
    }

    @Override
    @Transactional
    public Example createExample(ExampleRequest request) {
        Example example = new Example();
        example.setName(request.getName());
        example.setValue(request.getValue());

        exampleMapper.insert(example);
        return example;
    }
}
```

5. **Controller 작성** (`controller/`)

```java

@RestController
@RequestMapping("/api/example")
@RequiredArgsConstructor
@Slf4j
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Example> examples = exampleService.getAllExamples();
        return ResponseEntity.ok(examples);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ExampleRequest request) {
        Example created = exampleService.createExample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

### 트랜잭션 관리

```java
@Transactional  // 읽기/쓰기 트랜잭션
@Transactional(readOnly = true)  // 읽기 전용 (성능 최적화)
@Transactional(rollbackFor = Exception.class)  // 모든 예외 롤백
```

### 예외 처리

```java
// 커스텀 예외
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// 글로벌 예외 핸들러
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }
}
```

### 로깅 (AOP)

```java

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.meomulm.*.controller.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Request: {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        log.info("Response: {}", result);
        return result;
    }
}
```

## 빌드 및 배포

### 빌드

```bash
# 개발 빌드
./gradlew clean build

# 프로덕션 빌드 (테스트 제외)
./gradlew clean build -x test

# 특정 프로파일로 빌드
./gradlew clean build -Pprofile=production
```

### JAR 파일 생성 위치

```
build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar
```

### 실행

```bash
# 기본 실행
java -jar build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar

# 프로파일 지정
java -jar -Dspring.profiles.active=production \
     build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar

# 환경변수 파일 지정
java -jar -Dspring.config.location=classpath:/application.properties,file:./.env \
     build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar

# 포트 변경
java -jar -Dserver.port=9090 \
     build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar
```

### Docker 배포

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/meomulm-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Docker 이미지 빌드
docker build -t meomulm-backend:latest .

# Docker 컨테이너 실행
docker run -d -p 8080:8080 \
  --env-file .env \
  --name meomulm-backend \
  meomulm-backend:latest
```

### 헬스체크

```bash
# 서버 상태 확인
curl http://localhost:8080/health

# 메트릭 확인 (Actuator 활성화 시)
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

## 테스트

### 단위 테스트

```bash
./gradlew test
```

### 통합 테스트

```bash
./gradlew integrationTest
```

### 테스트 커버리지

```bash
./gradlew jacocoTestReport
```

## 트러블슈팅

### 일반적인 문제 해결

**1. 데이터베이스 연결 실패**

```bash
# PostgreSQL 실행 확인
sudo systemctl status postgresql

# 연결 테스트
psql -h localhost -U your_username -d meomulm

# config.properties의 DB_URL, DB_USERNAME, DB_PASSWORD 확인
```

**2. JWT 토큰 에러**

```bash
# JWT_SECRET이 256비트 이상인지 확인
# .env 파일의 JWT_SECRET 값 검증
```

**3. 포트 충돌**

```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080
netstat -ano | findstr :8080  # Windows

# 프로세스 종료 또는 다른 포트 사용
```

**4. 파일 업로드 실패**

```bash
# 업로드 디렉토리 권한 확인
ls -la ~/Desktop/meomulm/profile_images

# 디렉토리 생성 및 권한 설정
mkdir -p ~/Desktop/meomulm/profile_images
chmod 755 ~/Desktop/meomulm/profile_images
```

**5. MyBatis 매퍼 오류**

```bash
# XML 경로 확인
# mybatis.mapper-locations=classpath:/mappers/**/*.xml

# 네임스페이스와 인터페이스 경로 일치 확인
```

## 보안

### 보안 고려사항

- SQL Injection 방지 (MyBatis PreparedStatement)
- XSS 방지 (입력값 검증 및 이스케이핑)
- CSRF 방지 (SameSite Cookie, CORS 설정)
- 비밀번호 암호화 (BCrypt)
- JWT 토큰 만료 관리
- HTTPS 사용 (프로덕션)
- 민감 정보 환경변수 관리

### CORS 설정

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```
