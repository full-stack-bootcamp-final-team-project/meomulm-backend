# 머묾 API 테스트 가이드

## 목차
1. [사전 준비](#사전-준비)
2. [Swagger UI 접속](#swagger-ui-접속)
3. [TestFavoriteController로 테스트하기 (권장)](#testfavoritecontroller로-테스트하기-권장)
4. [FavoriteController로 테스트하기](#favoritecontroller로-테스트하기)
5. [API 엔드포인트 정리](#api-엔드포인트-정리)

---

## 사전 준비

### 1. Swagger 의존성 추가

`build.gradle` 파일에 다음 의존성을 추가하세요:

```gradle
dependencies {
    // Swagger (SpringDoc OpenAPI)
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    
    // 기타 의존성...
}
```

의존성 추가 후:
1. Gradle 새로고침 (IntelliJ: 우클릭 → Reload Gradle Project)
2. 애플리케이션 재시작

### 3. Swagger 설정 파일 생성

`src/main/java/com/meomulm/config/SwaggerConfig.java` 파일을 생성하세요:

```java
package com.meomulm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("머묾 API 문서")
                        .description("숙박 예약 서비스 REST API 명세서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("머묾 개발팀")
                                .email("dev@meomulm.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 제외)")
                        )
                );
    }
}
```

#### SwaggerConfig.java 역할

이 설정 파일은 Swagger UI의 외관과 보안 인증 방식을 정의합니다:

1. **API 문서 기본 정보 설정** (`.info()`)
    - `title`: Swagger UI 상단에 표시되는 제목
    - `description`: API 문서의 설명
    - `version`: API 버전 정보
    - `contact`: 개발팀 연락처 정보

2. **JWT 인증 설정** (`.addSecurityItem()`, `.components()`)
    - Swagger UI 우측 상단의 **Authorize** 버튼 생성
    - Bearer Token 인증 방식 활성화
    - 사용자가 JWT 토큰을 입력하면 모든 API 요청에 자동으로 `Authorization: Bearer {token}` 헤더 추가
    - `bearerFormat: "JWT"`: 토큰 형식을 JWT로 지정
    - `description`: Authorize 버튼 클릭 시 안내 문구

3. **전역 보안 적용**
    - 모든 API 엔드포인트에 기본적으로 인증 필요 표시 (자물쇠 아이콘)
    - 개별 Controller에서 `@SecurityRequirement(name = "Bearer Authentication")` 사용 가능

#### 설정 커스터마이징

프로젝트에 맞게 수정 가능한 부분:

```java
.title("여러분의 프로젝트명")           // 프로젝트명으로 변경
.description("여러분의 API 설명")       // API 설명 변경
.version("v1.0.0")                     // 버전 정보 변경
.contact(new Contact()
    .name("팀 이름")                   // 팀 이름 변경
    .email("team@example.com"))        // 이메일 변경
```

---

## 사전 준비

### 1. 데이터베이스 테스트 데이터 준비

API 테스트를 위해서는 데이터베이스에 테스트 데이터가 있어야 합니다.

```sql
-- 1. 테스트 사용자 추가 (user_id = 1)
INSERT INTO users (user_id, user_email, user_password, user_name, user_phone, created_at)
VALUES (1, 'test@example.com', 'password123', '테스트유저', '010-1234-5678', CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO NOTHING;

-- 2. 테스트 숙소 추가 (accommodation_id = 10)
INSERT INTO accommodation (accommodation_id, accommodation_name, accommodation_address)
VALUES (10, '서울 호텔', '서울시 강남구')
ON CONFLICT (accommodation_id) DO NOTHING;

-- 참고: 실제 테이블 구조에 맞게 컬럼명과 값을 수정하세요
```

**중요**: 위 SQL은 예시입니다. 실제 테이블 스키마에 맞게 수정해서 사용하세요.

### 2. Swagger 의존성 추가

애플리케이션 실행 후 브라우저에서 다음 URL 중 하나로 접속:

```
http://localhost:8080/swagger-ui.html
또는
http://localhost:8080/swagger-ui/index.html
```

---

## TestFavoriteController로 테스트하기 (권장)

> **TestFavoriteController**는 Swagger 문서화가 완벽하게 되어있어 테스트하기 편리합니다.

### 1단계: JWT 토큰 발급

1. Swagger UI에서 **Test** 섹션 찾기
2. **GET /api/test/token** 엔드포인트 클릭
3. "Try it out" 버튼 클릭
4. 파라미터 입력 (또는 기본값 사용):
    - `userId`: 1
    - `email`: test@example.com
5. "Execute" 버튼 클릭
6. Response에서 **`token`** 값 복사
   ```json
   {
     "success": true,
     "userId": 1,
     "email": "test@example.com",
     "token": "eyJhbGciOiJIUzI1NiJ9...",  // 이 값을 복사!
     "authorizationHeader": "Bearer eyJhbGciOiJIUzI1NiJ9...",
     "instruction": "위의 'token' 값만 복사해서 Swagger Authorize에 입력하세요"
   }
   ```

### 2단계: Swagger 인증 설정

1. Swagger UI 우측 상단의 **Authorize** 버튼 (🔓 자물쇠 아이콘) 클릭
2. "Value" 입력란에 복사한 **토큰만** 붙여넣기 (**Bearer 제외**)
   ```
   eyJhbGciOiJIUzI1NiJ9...
   ```
3. **Authorize** 버튼 클릭
4. **Close** 버튼으로 창 닫기
5. 아이콘이 🔒로 변경되면 인증 완료!

### 3단계: Favorite API 테스트

이제 **Favorite** 섹션에서 API를 테스트할 수 있습니다:

#### 찜 목록 조회
- **GET /api/test/favorite**
- "Try it out" → "Execute"
- Authorization 헤더는 자동으로 추가됨

#### 찜 추가
- **POST /api/test/favorite/{accommodationId}**
- "Try it out" 클릭
- `accommodationId` 입력 (예: 10)
- "Execute"

#### 찜 삭제
- **DELETE /api/test/favorite/{favoriteId}**
- "Try it out" 클릭
- `favoriteId` 입력 (예: 1)
- "Execute"

### 토큰 검증 테스트 (선택사항)

발급받은 토큰이 유효한지 확인하려면:

1. **GET /api/test/validate** 엔드포인트 사용
2. `token` 파라미터에 발급받은 토큰 입력
3. "Execute"로 유효성 확인

---

## FavoriteController로 테스트하기

> **FavoriteController**는 Swagger 문서화가 되어있지 않지만, 동일한 방식으로 테스트 가능합니다.

### 방법 1: Swagger UI에서 직접 테스트

FavoriteController는 Swagger에 기본적으로 표시되지만 문서화가 없습니다.

1. **위의 1~2단계 동일하게 진행** (토큰 발급 및 인증)
2. Swagger UI에서 **favorite-controller** 섹션 찾기
3. 다음 엔드포인트 테스트:
    - **GET /api/favorite** - 찜 목록 조회
    - **POST /api/favorite/{accommodationId}** - 찜 추가
    - **DELETE /api/favorite/{favoriteId}** - 찜 삭제

### 방법 2: cURL로 테스트

터미널에서 직접 API 호출:

```bash
# 1. 토큰 발급
curl -X GET "http://localhost:8080/api/test/token?userId=1&email=test@example.com"

# 응답에서 token 값을 복사하여 아래 명령어의 YOUR_TOKEN 부분에 붙여넣기

# 2. 찜 목록 조회
curl -X GET "http://localhost:8080/api/favorite" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 3. 찜 추가
curl -X POST "http://localhost:8080/api/favorite/10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 4. 찜 삭제
curl -X DELETE "http://localhost:8080/api/favorite/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 방법 3: Postman으로 테스트

1. **토큰 발급**
    - Method: GET
    - URL: `http://localhost:8080/api/test/token?userId=1&email=test@example.com`
    - Send 클릭
    - Response에서 `token` 값 복사

2. **인증 헤더 설정**
    - Headers 탭에서 추가:
        - Key: `Authorization`
        - Value: `Bearer YOUR_TOKEN` (실제 토큰으로 교체)

3. **API 호출**
    - 찜 목록 조회: GET `http://localhost:8080/api/favorite`
    - 찜 추가: POST `http://localhost:8080/api/favorite/10`
    - 찜 삭제: DELETE `http://localhost:8080/api/favorite/1`

### 방법 4: FavoriteController에 Swagger 문서 추가

FavoriteController에도 TestFavoriteController처럼 Swagger 어노테이션을 추가하려면:

```java
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class FavoriteController {
    
    @Operation(summary = "찜 목록 조회", description = "로그인한 사용자의 찜 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SelectFavorite>> getFavorites(
        @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        // ...
    }
    
    // 나머지 메서드에도 @Operation 추가
}
```

---

## API 엔드포인트 정리

### 테스트용 API (TestFavoriteController)
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-------|
| GET | /api/test/token | JWT 토큰 발급 | X     |
| GET | /api/test/validate | JWT 토큰 검증 | X     |
| GET | /api/test/me | 현재 인증된 사용자 정보 | O     |
| GET | /api/test/favorite | 찜 목록 조회 | O     |
| POST | /api/test/favorite/{accommodationId} | 찜 추가 | O     |
| DELETE | /api/test/favorite/{favoriteId} | 찜 삭제 | O     |

### 실제 API (FavoriteController)
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-------|
| GET | /api/favorite | 찜 목록 조회 | O  |
| POST | /api/favorite/{accommodationId} | 찜 추가 | O  |
| DELETE | /api/favorite/{favoriteId} | 찜 삭제 | O  |

---

## 문제 해결

### 401 Unauthorized 에러
- 토큰이 만료되었거나 유효하지 않음
- `/api/test/token`에서 새 토큰 발급 받기
- Authorize에 토큰을 올바르게 입력했는지 확인 (Bearer 제외)

### 400 Bad Request - "Required request header 'Authorization' is not present"
**원인**: Swagger에서 Authorize를 하지 않고 인증이 필요한 API를 호출했을 때 발생

**해결방법**:
1. Swagger UI 우측 상단의 **Authorize** 버튼 (🔓) 클릭
2. `/api/test/token`에서 토큰 발급
3. 발급받은 토큰을 Authorize 창에 입력 (Bearer 제외)
4. **Authorize** 버튼 클릭하여 인증 완료
5. 아이콘이 🔒로 변경되었는지 확인
6. API 재시도

**참고**: 자물쇠 아이콘이 🔒로 잠겨있으면 인증된 상태입니다.

### 500 Internal Server Error - Foreign Key Constraint 에러
**에러 메시지**:
```
ERROR: insert or update on table "favorite" violates foreign key constraint
Detail: Key (user_id)=(1) is not present in table "users".
```

**원인**: 데이터베이스에 테스트 데이터(사용자 또는 숙소)가 없음

**해결방법**:
1. 데이터베이스에 테스트 사용자 추가 (위의 "데이터베이스 테스트 데이터 준비" 섹션 참조)
2. 데이터베이스에 테스트 숙소 추가
3. API 재시도

**빠른 해결**:
```sql
-- PostgreSQL 기준
INSERT INTO users (user_id, user_email, user_password, user_name, created_at)
VALUES (1, 'test@example.com', 'password', '테스트유저', CURRENT_TIMESTAMP);

INSERT INTO accommodation (accommodation_id, accommodation_name, accommodation_address)
VALUES (10, '테스트숙소', '서울시 강남구');
```

### 404 Not Found 에러
- 찜 목록이 없거나 존재하지 않는 리소스 요청
- 먼저 찜을 추가한 후 조회 테스트

### 403 Forbidden 에러
- 다른 사용자의 찜을 삭제하려고 시도
- 본인의 찜만 삭제 가능

### Swagger UI가 안 보일 때
- 애플리케이션이 정상 실행 중인지 확인
- 포트 번호 확인 (기본 8080)
- 브라우저 캐시 삭제 후 재접속

---

## 참고사항

- **TestFavoriteController**: 개발/테스트 환경 전용, Swagger 문서화 완료
- **FavoriteController**: 실제 프로덕션 환경용, 문서화 미포함
- JWT 토큰 만료 시간: `application.properties`의 `jwt.expiration` 설정값 참조
- 운영 환경에서는 TestController 비활성화 권장

---

## 추가 리소스

- Swagger 공식 문서: https://swagger.io/docs/
- JWT 공식 문서: https://jwt.io/
- Spring Boot Swagger 설정: https://springdoc.org/