# Elasticsearch 검색 엔진 통합 가이드


## 개요

이 프로젝트는 **Elasticsearch 8.12**와 **Nori 한글 형태소 분석기**를 사용하여 숙소, 리뷰, 상품에 대한 고성능 전문 검색 기능을 구현합니다.

### 기술 스택
- **검색 엔진**: Elasticsearch 8.12.0
- **한글 분석**: Nori Tokenizer (analysis-nori plugin)
- **데이터베이스**: PostgreSQL 16
- **프레임워크**: Spring Boot 3.2.1
- **ORM**: MyBatis
- **컨테이너**: Docker & Docker Compose

### 아키텍처
```
PostgreSQL (원본 데이터)
    ↓
MyBatis Mapper (데이터 조회)
    ↓
ElasticsearchSyncService (동기화)
    ↓
Elasticsearch (검색 엔진)
    ↓
ElasticsearchService (검색 로직)
    ↓
Controller (API 제공)
```

---

## 주요 기능

### 1. 숙소 검색 (AccommodationElasticsearchService)
- **한글 키워드 검색**: 숙소명, 주소 기반 자연어 검색 (Nori 분석기)
- **위치 기반 검색**: GeoPoint를 활용한 반경 5km 내 검색
- **다중 필터링**: 숙소 타입, 가격 범위, 평점 조합 필터
- **정렬**: 가격, 평점, 리뷰 수 기준 정렬
- **자동완성**: 검색어 입력 시 실시간 추천

### 2. 리뷰 검색 (ReviewElasticsearchService)
- **숙소별 리뷰 조회**: 최신순 정렬
- **사용자별 리뷰 조회**: 내가 작성한 리뷰
- **키워드 검색**: 리뷰 내용 기반 한글 검색
- **평점 필터링**: 특정 평점 이상 리뷰 조회

### 3. 데이터 동기화 (ElasticsearchSyncService)
- **실시간 동기화**: 데이터 생성/수정/삭제 시 즉시 반영
- **배치 동기화**: 매일 새벽 자동 전체 동기화
    - 숙소: 매일 새벽 3시
    - 리뷰: 매일 새벽 4시
- **Bulk 작업**: 대량 데이터 효율적 처리

---

## 설치 및 실행

### 1️⃣ Elasticsearch 실행 (개발 환경)

**보안 비활성화 버전 (권장 - 개발용)**
```bash
cd /path/to/project

# Elasticsearch 및 Kibana 실행 (보안 없음)
docker-compose -f docker-compose-elasticsearch-no-security.yml up -d

# 상태 확인 (인증 불필요)
curl http://localhost:9200/_cluster/health

# Kibana 접속
# http://localhost:5601 (로그인 불필요)
```

**보안 활성화 버전 (프로덕션용)**
```bash
# Elasticsearch 및 Kibana 실행 (보안 활성화)
docker-compose -f docker-compose-elasticsearch.yml up -d

# 상태 확인 (인증 필요)
curl -u elastic:meomulm2026! http://localhost:9200/_cluster/health

# Kibana 접속
# http://localhost:5601
# Username: elastic
# Password: meomulm2026!
```

### 2️⃣ Nori 한글 분석기 설치

```bash
# Elasticsearch 컨테이너에 접속
docker exec -it meomulm-elasticsearch bash

# Nori 플러그인 설치
elasticsearch-plugin install -b analysis-nori

# 컨테이너 재시작
exit
docker restart meomulm-elasticsearch

# 설치 확인 (30초 대기 후)
docker exec meomulm-elasticsearch elasticsearch-plugin list
# 출력: analysis-nori
```

### 3️⃣ 애플리케이션 설정

**보안 비활성화 환경** (`src/main/resources/application.properties`):
```properties
# Elasticsearch Configuration (보안 없음)
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
```

**보안 활성화 환경** (`src/main/resources/application.properties`):
```properties
# Elasticsearch Configuration (보안 활성화)
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=meomulm2026!
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
```

### 4️⃣ 애플리케이션 실행

```bash
# Spring Boot 애플리케이션 시작
./gradlew bootRun

# 또는 IntelliJ에서 실행
```

애플리케이션 시작 시:
1. ElasticsearchIndexInitializer가 자동으로 인덱스 생성
2. Nori 분석기 설정 자동 적용
3. 매핑(Mapping) 자동 생성

### 5️⃣ 초기 데이터 동기화

애플리케이션이 실행되면 스케줄러가 자동으로 동작하지만, 즉시 동기화가 필요한 경우:

**방법 1: 스케줄러 대기**
- 숙소: 매일 새벽 3시 자동 동기화
- 리뷰: 매일 새벽 4시 자동 동기화

**방법 2: 수동 호출** (코드 수정 필요)
```java
// ElasticsearchSyncService에 임시 API 추가 또는
// @PostConstruct 어노테이션으로 시작 시 자동 실행
```

---

## 파일 구조 및 설명

### 프로젝트 구조
```
meomulm-backend/
├── docker/
│   ├── Dockerfile                                    # Spring Boot 애플리케이션 Docker 이미지
│   ├── docker-compose.yml                            # 전체 서비스 통합 (PostgreSQL + Elasticsearch + Backend)
│   ├── docker-compose-elasticsearch.yml              # Elasticsearch + Kibana (보안 활성화)
│   └── docker-compose-elasticsearch-no-security.yml  # Elasticsearch + Kibana (보안 비활성화)
│
├── src/main/resources/
│   └── application.properties                        # Elasticsearch 연결 설정
│
└── src/main/java/com/meomulm/
    ├── common/config/
    │   ├── ElasticsearchConfig.java                  # Elasticsearch 클라이언트 설정
    │   └── ElasticsearchIndexInitializer.java        # 인덱스 자동 생성 및 매핑 설정
    │
    ├── common/service/
    │   └── ElasticsearchSyncService.java             # PostgreSQL ↔ Elasticsearch 동기화
    │
    ├── accommodation/
    │   ├── model/document/
    │   │   └── AccommodationDocument.java            # 숙소 Elasticsearch Document
    │   ├── model/repository/
    │   │   └── AccommodationElasticsearchRepository.java  # 숙소 검색 Repository
    │   └── model/service/
    │       └── AccommodationElasticsearchService.java     # 숙소 검색 비즈니스 로직
    │
    ├── product/model/document/
    │   └── ProductDocument.java                      # 상품(객실) Elasticsearch Document
    │
    └── review/
        ├── model/document/
        │   └── ReviewDocument.java                   # 리뷰 Elasticsearch Document
        ├── model/repository/
        │   └── ReviewElasticsearchRepository.java    # 리뷰 검색 Repository
        └── model/service/
            └── ReviewElasticsearchService.java       # 리뷰 검색 비즈니스 로직
```

---

### 파일별 상세 설명

#### 1. **Dockerfile** (Docker 이미지 정의)
**목적**: Spring Boot 애플리케이션을 Docker 컨테이너로 실행하기 위한 이미지 정의

**주요 내용**:
- OpenJDK 21 기반 이미지 사용
- Gradle로 빌드된 JAR 파일 실행
- 포트 8080 노출

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

#### 2. **application.properties** (애플리케이션 설정)
**목적**: Elasticsearch 연결 및 타임아웃 설정

**주요 설정**:
```properties
# Elasticsearch 접속 정보
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic              # 보안 활성화 시에만
spring.elasticsearch.password=meomulm2026!         # 보안 활성화 시에만

# 연결 타임아웃 설정
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
```

**보안 비활성화 환경**에서는 `username`, `password` 제거 필요!

---

#### 3. **AccommodationDocument.java** (82줄)
**목적**: 숙소 데이터를 Elasticsearch에 저장하기 위한 Document 모델

**주요 필드**:
- `@Id`: Elasticsearch 문서 ID
- `@Field(analyzer = "nori")`: 한글 형태소 분석 적용
- `@GeoPointField`: 위치 정보 저장 (위도/경도)

```java
@Document(indexName = "accommodations")
public class AccommodationDocument {
    @Id
    private Integer accommodationId;
    
    @Field(type = FieldType.Text, analyzer = "nori")
    private String accommodationName;  // 한글 검색 가능
    
    @GeoPointField
    private GeoPoint location;  // 위치 기반 검색
    
    private Integer minPrice;
    private Double averageRating;
    // ...
}
```

**특징**:
- Nori 분석기로 "강남역 근처 호텔" → "강남", "역", "근처", "호텔"로 토큰화
- GeoPoint로 "내 위치에서 5km 이내" 검색 가능

---

#### 4. **ProductDocument.java** (59줄)
**목적**: 상품(객실) 데이터를 Elasticsearch에 저장하기 위한 Document 모델

**주요 필드**:
```java
@Document(indexName = "products")
public class ProductDocument {
    @Id
    private Integer productId;
    
    @Field(type = FieldType.Text, analyzer = "nori")
    private String productName;  // "디럭스 트윈룸" 등 한글 검색
    
    private Integer productPrice;
    private Boolean isAvailable;
    // ...
}
```

---

#### 5. **ReviewDocument.java** (48줄)
**목적**: 리뷰 데이터를 Elasticsearch에 저장하기 위한 Document 모델

**주요 필드**:
```java
@Document(indexName = "reviews")
public class ReviewDocument {
    @Id
    private Integer reviewId;
    
    @Field(type = FieldType.Text, analyzer = "nori")
    private String reviewContent;  // "깨끗하고 친절해요" 등 리뷰 내용 검색
    
    private Double rating;
    private String createdAt;
    // ...
}
```

---

#### 6. **docker-compose.yml** (81줄)
**목적**: PostgreSQL + Elasticsearch + Spring Boot 애플리케이션 통합 실행

**서비스 구성**:
1. **postgres**: PostgreSQL 16 데이터베이스
2. **elasticsearch**: Elasticsearch 8.12 (보안 활성화)
3. **backend**: Spring Boot 애플리케이션

```yaml
services:
  postgres:
    image: postgres:16-alpine
    ports:
      - "5432:5432"
  
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.12.0
    environment:
      - xpack.security.enabled=true
      - ELASTIC_PASSWORD=meomulm2026!
    ports:
      - "9200:9200"
  
  backend:
    build: .
    depends_on:
      - postgres
      - elasticsearch
    ports:
      - "8080:8080"
```

**사용 시나리오**: 전체 시스템을 한 번에 실행

---

#### 7. **docker-compose-elasticsearch.yml** (70줄)
**목적**: Elasticsearch + Kibana만 실행 (보안 활성화)

**서비스 구성**:
1. **elasticsearch**: 보안 활성화, 인증 필요
2. **kibana**: 데이터 시각화 및 관리 도구
3. **elasticsearch-nori-plugin**: Nori 플러그인 설치 컨테이너

```yaml
services:
  elasticsearch:
    environment:
      - xpack.security.enabled=true
      - ELASTIC_PASSWORD=meomulm2026!
  
  kibana:
    environment:
      - ELASTICSEARCH_USERNAME=elastic
      - ELASTICSEARCH_PASSWORD=meomulm2026!
```

**접속**:
- Elasticsearch: `http://localhost:9200` (elastic / meomulm2026!)
- Kibana: `http://localhost:5601` (elastic / meomulm2026!)

---

#### 8. **docker-compose-elasticsearch-no-security.yml** (52줄)
**목적**: Elasticsearch + Kibana만 실행 (보안 비활성화 - 개발용)

**차이점**:
```yaml
elasticsearch:
  environment:
    - xpack.security.enabled=false  # 인증 불필요
```

**장점**:
- 간편한 개발 환경
- 인증 없이 바로 접속
- 설정 간단

**단점**:
- 프로덕션 환경 부적합
- 보안 취약

---

#### 9. **ElasticsearchConfig.java** (75줄)
**목적**: Elasticsearch 클라이언트 Bean 설정

**주요 기능**:
1. RestClient 생성 (HTTP 통신)
2. ElasticsearchClient 생성 (Java API)
3. 인증 정보 설정 (보안 활성화 시)

```java
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.meomulm.*.model.repository")
public class ElasticsearchConfig {
    
    @Bean
    public RestClient restClient() {
        // 인증 설정
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(username, password)
        );
        
        return RestClient.builder(new HttpHost(hostname, port, "http"))
            .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            )
            .build();
    }
    
    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
```

---

#### 10. **ElasticsearchIndexInitializer.java** (238줄)
**목적**: 애플리케이션 시작 시 인덱스 자동 생성 및 Nori 분석기 설정

**실행 시점**: `CommandLineRunner` 구현 → 애플리케이션 시작 직후 실행

**주요 기능**:
1. 인덱스 존재 여부 확인
2. Nori 분석기 설정
3. 매핑(Mapping) 정의
4. 인덱스 생성

```java
@Component
public class ElasticsearchIndexInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        createAccommodationIndex();  // accommodations 인덱스 생성
        createReviewIndex();          // reviews 인덱스 생성
        createProductIndex();         // products 인덱스 생성
    }
    
    private void createAccommodationIndex() {
        // 1. 인덱스 존재 확인
        BooleanResponse exists = elasticsearchClient.indices()
            .exists(e -> e.index("accommodations"));
        
        if (exists.value()) {
            log.info("인덱스 'accommodations' 가 이미 존재합니다.");
            return;
        }
        
        // 2. Nori 분석기 설정
        String settings = """
            {
              "analysis": {
                "analyzer": {
                  "nori": {
                    "type": "custom",
                    "tokenizer": "nori_tokenizer",
                    "filter": ["lowercase", "nori_part_of_speech"]
                  }
                }
              }
            }
            """;
        
        // 3. 매핑 정의
        String mapping = """
            {
              "properties": {
                "accommodationName": { 
                  "type": "text",
                  "analyzer": "nori"
                },
                "location": { "type": "geo_point" }
              }
            }
            """;
        
        // 4. 인덱스 생성
        elasticsearchClient.indices().create(c -> c
            .index("accommodations")
            .settings(s -> s.withJson(new StringReader(settings)))
            .mappings(m -> m.withJson(new StringReader(mapping)))
        );
    }
}
```

---

#### 11. **AccommodationElasticsearchRepository.java** (42줄)
**목적**: Spring Data Elasticsearch를 사용한 숙소 검색 Repository

**주요 메서드**:
```java
@Repository
public interface AccommodationElasticsearchRepository 
        extends ElasticsearchRepository<AccommodationDocument, Integer> {
    
    // 커스텀 쿼리 - 숙소명 또는 주소로 검색
    @Query("{\"bool\": {\"should\": [" +
           "{\"match\": {\"accommodationName\": {\"query\": \"?0\", \"boost\": 2}}}," +
           "{\"match\": {\"accommodationAddress\": \"?0\"}}" +
           "]}}")
    List<AccommodationDocument> searchByKeyword(String keyword);
    
    // 타입 필터링
    List<AccommodationDocument> findByAccommodationType(String type);
    
    // 가격 범위 검색
    List<AccommodationDocument> findByMinPriceBetween(Integer minPrice, Integer maxPrice);
    
    // 평점 필터
    List<AccommodationDocument> findByAverageRatingGreaterThanEqual(Double rating);
}
```

**특징**:
- Spring Data JPA와 유사한 메서드 네이밍 규칙
- `@Query` 어노테이션으로 복잡한 쿼리 작성 가능

---

#### 12. **ElasticsearchSyncService.java** (272줄)
**목적**: PostgreSQL과 Elasticsearch 간 데이터 동기화

**주요 기능**:

1️⃣ **실시간 동기화**
```java
// 숙소 생성/수정 시 호출
public void syncAccommodation(Integer accommodationId) {
    // PostgreSQL에서 데이터 조회
    List<SearchAccommodationResponse> results = 
        accommodationMapper.selectRecentAccommodations(ids);
    
    // Elasticsearch에 인덱싱
    elasticsearchClient.index(i -> i
        .index("accommodations")
        .id(String.valueOf(accommodationId))
        .document(document)
    );
}
```

2️⃣ **배치 동기화** (스케줄링)
```java
// 매일 새벽 3시 실행
@Scheduled(cron = "0 0 3 * * *")
public void syncAllAccommodations() {
    // PostgreSQL에서 전체 숙소 조회
    List<SearchAccommodationResponse> accommodations = 
        accommodationMapper.selectAccommodations(request);
    
    // Bulk 작업으로 일괄 인덱싱
    BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(operations));
    elasticsearchClient.bulk(bulkRequest);
}
```

3️⃣ **삭제 동기화**
```java
public void deleteAccommodation(Integer accommodationId) {
    elasticsearchClient.delete(d -> d
        .index("accommodations")
        .id(String.valueOf(accommodationId))
    );
}
```

---

#### 13. **ReviewElasticsearchRepository.java** (39줄)
**목적**: Spring Data Elasticsearch를 사용한 리뷰 검색 Repository

```java
@Repository
public interface ReviewElasticsearchRepository 
        extends ElasticsearchRepository<ReviewDocument, Integer> {
    
    // 숙소별 리뷰 조회
    List<ReviewDocument> findByAccommodationId(Integer accommodationId);
    
    // 사용자별 리뷰 조회
    List<ReviewDocument> findByUserId(Integer userId);
    
    // 리뷰 내용 검색 (한글)
    @Query("{\"match\": {\"reviewContent\": \"?0\"}}")
    List<ReviewDocument> searchByContent(String keyword);
    
    // 평점 범위 검색
    List<ReviewDocument> findByRatingBetween(Double minRating, Double maxRating);
}
```

---

#### 14. **ReviewElasticsearchService.java** (187줄)
**목적**: 리뷰 검색 비즈니스 로직

**주요 메서드**:

1️⃣ **숙소별 리뷰 조회**
```java
public List<ReviewDocument> getReviewsByAccommodationId(Integer accommodationId) {
    SearchRequest searchRequest = SearchRequest.of(s -> s
        .index("reviews")
        .query(q -> q
            .term(t -> t.field("accommodationId").value(accommodationId))
        )
        .sort(sort -> sort
            .field(f -> f.field("createdAt").order(SortOrder.Desc))
        )
    );
    
    SearchResponse<ReviewDocument> response = 
        elasticsearchClient.search(searchRequest, ReviewDocument.class);
    
    return response.hits().hits().stream()
        .map(Hit::source)
        .collect(Collectors.toList());
}
```

2️⃣ **키워드 검색**
```java
public List<ReviewDocument> searchReviewsByKeyword(String keyword) {
    SearchRequest searchRequest = SearchRequest.of(s -> s
        .index("reviews")
        .query(q -> q
            .match(m -> m.field("reviewContent").query(keyword))
        )
    );
    
    return elasticsearchClient.search(searchRequest, ReviewDocument.class)
        .hits().hits().stream()
        .map(Hit::source)
        .collect(Collectors.toList());
}
```

---

## API 엔드포인트

### 숙소 검색 API

#### 1. 통합 검색 (Elasticsearch)
```http
GET /api/accommodation/search?keyword=강남&minPrice=50000&maxPrice=200000&minRating=4.0&sortBy=rating
```

**파라미터**:
| 파라미터 | 타입 | 설명 | 예시 |
|---------|------|------|------|
| `keyword` | String | 검색 키워드 (숙소명, 주소) | "강남", "명동 호텔" |
| `latitude` | Double | 현재 위도 (위치 기반 검색) | 37.5665 |
| `longitude` | Double | 현재 경도 (위치 기반 검색) | 126.9780 |
| `accommodationType` | String | 숙소 타입 | "호텔", "모텔" |
| `minPrice` | Integer | 최소 가격 | 50000 |
| `maxPrice` | Integer | 최대 가격 | 200000 |
| `minRating` | Double | 최소 평점 | 4.0 |
| `sortBy` | String | 정렬 기준 | price_asc, price_desc, rating, review_count |
| `page` | Integer | 페이지 번호 | 0, 1, 2... |

**응답 예시**:
```json
[
  {
    "accommodationId": 123,
    "accommodationName": "강남 비즈니스 호텔",
    "accommodationAddress": "서울특별시 강남구 테헤란로 123",
    "accommodationType": "호텔",
    "minPrice": 80000,
    "averageRating": 4.5,
    "reviewCount": 152,
    "mainImage": "https://example.com/image.jpg"
  }
]
```

#### 2. 위치 기반 검색
```http
POST /api/accommodation/map
Content-Type: application/json

{
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

**응답**: 현재 위치에서 반경 5km 이내 숙소 리스트

#### 3. 자동완성
```http
GET /api/accommodation/autocomplete?prefix=강남
```

**응답**:
```json
[
  "강남역",
  "강남구",
  "강남 호텔"
]
```

#### 4. 인기 숙소 (지역별)
```http
GET /api/accommodation/popular?accommodationAddress=서울특별시 강남구
```

**응답**: 해당 지역의 평점/리뷰 수 기준 인기 숙소 12개

---

### 리뷰 검색 API

#### 1. 숙소별 리뷰 조회
```http
GET /api/review/accommodationId/123
```

#### 2. 내 리뷰 조회
```http
GET /api/review
Authorization: Bearer {JWT_TOKEN}
```

#### 3. 리뷰 키워드 검색
```http
GET /api/review/search?keyword=깨끗
```

#### 4. 평점별 필터링
```http
GET /api/review/filter/123?minRating=4.0
```

---

## 인덱스 구조

### 1. accommodations 인덱스

```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "nori": {
          "type": "custom",
          "tokenizer": "nori_tokenizer",
          "filter": ["lowercase", "nori_part_of_speech"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "accommodationId": { "type": "integer" },
      "accommodationName": { 
        "type": "text",
        "analyzer": "nori",
        "fields": {
          "keyword": { "type": "keyword" }
        }
      },
      "accommodationAddress": { 
        "type": "text",
        "analyzer": "nori"
      },
      "accommodationType": { "type": "keyword" },
      "location": { "type": "geo_point" },
      "accommodationLatitude": { "type": "double" },
      "accommodationLongitude": { "type": "double" },
      "minPrice": { "type": "integer" },
      "averageRating": { "type": "double" },
      "reviewCount": { "type": "integer" },
      "mainImage": { "type": "text" },
      "facilities": { "type": "keyword" },
      "createdAt": { "type": "date" },
      "updatedAt": { "type": "date" }
    }
  }
}
```

### 2. reviews 인덱스

```json
{
  "mappings": {
    "properties": {
      "reviewId": { "type": "integer" },
      "accommodationId": { "type": "integer" },
      "userId": { "type": "integer" },
      "userName": { "type": "text" },
      "reviewContent": { 
        "type": "text",
        "analyzer": "nori"
      },
      "rating": { "type": "double" },
      "createdAt": { "type": "date" },
      "updatedAt": { "type": "date" }
    }
  }
}
```

### 3. products 인덱스

```json
{
  "mappings": {
    "properties": {
      "productId": { "type": "integer" },
      "accommodationId": { "type": "integer" },
      "productName": { 
        "type": "text",
        "analyzer": "nori"
      },
      "productDescription": { 
        "type": "text",
        "analyzer": "nori"
      },
      "productPrice": { "type": "integer" },
      "maxGuests": { "type": "integer" },
      "facilities": { "type": "keyword" },
      "mainImage": { "type": "text" },
      "isAvailable": { "type": "boolean" },
      "createdAt": { "type": "date" },
      "updatedAt": { "type": "date" }
    }
  }
}
```

---

## 검색 쿼리 예제

### 1. 복합 검색 (키워드 + 위치 + 필터)

**요구사항**: "강남"이 포함된 숙소를 현재 위치 5km 내에서 검색하되, 가격은 5만원~20만원, 평점 4.0 이상

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("accommodations")
    .query(q -> q
        .bool(b -> b
            // 키워드 검색 (숙소명에 2배 가중치)
            .should(sh -> sh
                .match(m -> m
                    .field("accommodationName")
                    .query("강남")
                    .boost(2.0f)
                )
            )
            .should(sh -> sh
                .match(m -> m
                    .field("accommodationAddress")
                    .query("강남")
                )
            )
            // 위치 필터 (5km 이내)
            .filter(f -> f
                .geoDistance(g -> g
                    .field("location")
                    .distance("5km")
                    .location(l -> l.latlon(ll -> ll
                        .lat(37.5665)
                        .lon(126.9780)
                    ))
                )
            )
            // 가격 필터
            .filter(f -> f
                .range(r -> r
                    .field("minPrice")
                    .gte(JsonData.of(50000))
                    .lte(JsonData.of(200000))
                )
            )
            // 평점 필터
            .filter(f -> f
                .range(r -> r
                    .field("averageRating")
                    .gte(JsonData.of(4.0))
                )
            )
        )
    )
    // 평점 높은 순 정렬
    .sort(sort -> sort
        .field(f -> f.field("averageRating").order(SortOrder.Desc))
    )
    .size(20)
);
```

### 2. 한글 검색 (Nori 분석기)

**검색어**: "깨끗한 호텔"
**분석 결과**: "깨끗하다" (원형) + "호텔"

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("reviews")
    .query(q -> q
        .match(m -> m
            .field("reviewContent")
            .query("깨끗한 호텔")
        )
    )
    .highlight(h -> h
        .fields("reviewContent", hf -> hf)
    )
);
```

### 3. 자동완성 (Phrase Prefix)

**입력**: "강남"
**추천**: "강남역", "강남구", "강남 호텔"

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("accommodations")
    .query(q -> q
        .matchPhrasePrefix(mp -> mp
            .field("accommodationName")
            .query("강남")
        )
    )
    .source(src -> src
        .filter(f -> f.includes("accommodationName"))
    )
    .size(10)
);
```

### 4. 위치 기반 정렬 (가까운 순)

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("accommodations")
    .sort(sort -> sort
        .geoDistance(g -> g
            .field("location")
            .location(l -> l.latlon(ll -> ll
                .lat(37.5665)
                .lon(126.9780)
            ))
            .order(SortOrder.Asc)
            .unit(DistanceUnit.Kilometers)
        )
    )
);
```

---

## 데이터 동기화

### 실시간 동기화 전략

**언제 동기화하나요?**
1. 숙소 생성/수정 시 → `syncAccommodation(id)` 호출
2. 리뷰 작성 시 → `syncReview(id)` 호출
3. 숙소 삭제 시 → `deleteAccommodation(id)` 호출

**예시 (Service Layer)**:
```java
@Service
public class AccommodationService {
    private final ElasticsearchSyncService syncService;
    
    public void createAccommodation(Accommodation accommodation) {
        // 1. PostgreSQL에 저장
        accommodationMapper.insert(accommodation);
        
        // 2. Elasticsearch에 동기화
        syncService.syncAccommodation(accommodation.getId());
    }
}
```

### 배치 동기화 스케줄

| 작업 | 시간 | 주기 | Cron 표현식 |
|------|------|------|------------|
| 숙소 전체 동기화 | 새벽 3시 | 매일 | `0 0 3 * * *` |
| 리뷰 전체 동기화 | 새벽 4시 | 매일 | `0 0 4 * * *` |

**배치 동기화 흐름**:
```
1. PostgreSQL에서 전체 데이터 조회
   ↓
2. Elasticsearch Document로 변환
   ↓
3. Bulk API로 일괄 인덱싱
   ↓
4. 성공/실패 로그 기록
```

---

## 성능 최적화

### 1. 인덱스 설정 최적화

```json
{
  "settings": {
    "number_of_shards": 1,          // 단일 샤드 (소규모 데이터)
    "number_of_replicas": 1,        // 복제본 1개 (읽기 성능 향상)
    "refresh_interval": "30s",      // 30초마다 새로고침 (기본 1s)
    "max_result_window": 10000      // 최대 결과 수
  }
}
```

### 2. 쿼리 최적화

**나쁜 예 (Query Context)**:
```java
// 모든 조건에서 스코어 계산 → 느림
.query(q -> q
    .bool(b -> b
        .must(m -> m.term(t -> t.field("type").value("호텔")))
        .must(m -> m.range(r -> r.field("price").gte(JsonData.of(50000))))
    )
)
```

**좋은 예 (Filter Context)**:
```java
// 필터는 스코어 계산 안함 → 빠름 + 캐싱
.query(q -> q
    .bool(b -> b
        .filter(f -> f.term(t -> t.field("type").value("호텔")))
        .filter(f -> f.range(r -> r.field("price").gte(JsonData.of(50000))))
    )
)
```

### 3. Bulk 작업 최적화

```java
// 나쁜 예: 하나씩 인덱싱
for (Accommodation acc : accommodations) {
    elasticsearchClient.index(i -> i
        .index("accommodations")
        .document(acc)
    );
}

// 좋은 예: Bulk API 사용
List<BulkOperation> operations = accommodations.stream()
    .map(acc -> BulkOperation.of(b -> b
        .index(idx -> idx.document(acc))
    ))
    .collect(Collectors.toList());

elasticsearchClient.bulk(b -> b
    .index("accommodations")
    .operations(operations)
);
```

### 4. Source Filtering (필요한 필드만 조회)

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("accommodations")
    .source(src -> src
        .filter(f -> f
            .includes("accommodationId", "accommodationName", "minPrice")
            .excludes("facilities", "mainImage")
        )
    )
);
```

---

## 트러블슈팅

### 문제 1: 연결 오류
```
Could not connect to Elasticsearch at http://localhost:9200
```

**원인**:
- Elasticsearch 컨테이너가 실행되지 않음
- 포트 충돌

**해결방법**:
```bash
# 1. 컨테이너 상태 확인
docker ps

# 2. Elasticsearch 로그 확인
docker logs meomulm-elasticsearch

# 3. 재시작
docker restart meomulm-elasticsearch

# 4. 포트 확인
netstat -ano | findstr :9200  # Windows
lsof -i :9200                  # Mac/Linux
```

---

### 문제 2: Nori 분석기 오류
```
[mapper_parsing_exception] Failed to parse mapping: analyzer [nori] has not been configured
```

**원인**: Nori 플러그인이 설치되지 않음

**해결방법**:
```bash
# 1. 플러그인 설치
docker exec meomulm-elasticsearch elasticsearch-plugin install -b analysis-nori

# 2. Elasticsearch 재시작
docker restart meomulm-elasticsearch

# 3. 설치 확인 (30초 대기 후)
docker exec meomulm-elasticsearch elasticsearch-plugin list

# 4. 인덱스 삭제 후 재생성
curl -X DELETE http://localhost:9200/accommodations
# 애플리케이션 재시작 → 인덱스 자동 생성
```

---

### 문제 3: 한글 검색 안됨
```
검색어: "깨끗한 호텔"
결과: 0건 (실제로는 데이터 있음)
```

**원인**:
- Nori 분석기가 적용되지 않음
- 인덱스 매핑 오류

**확인 방법**:
```bash
# Kibana Dev Tools에서 실행
GET /accommodations/_mapping

# accommodationName 필드에 "analyzer": "nori" 확인
```

**해결방법**:
```bash
# 1. 인덱스 삭제
curl -X DELETE http://localhost:9200/accommodations

# 2. 애플리케이션 재시작 (인덱스 자동 재생성)

# 3. 데이터 재동기화
```

---

### 문제 4: 메모리 부족
```
OutOfMemoryError: Java heap space
```

**원인**: Elasticsearch JVM 힙 메모리 부족

**해결방법**:
```yaml
# docker-compose-elasticsearch.yml 수정
services:
  elasticsearch:
    environment:
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"  # 1g → 2g로 증가
```

---

### 문제 5: 인증 오류
```
security_exception: missing authentication credentials
```

**원인**:
- 보안 활성화 환경에서 인증 정보 누락
- 잘못된 비밀번호

**해결방법 1** (보안 비활성화 - 개발용):
```bash
# docker-compose-elasticsearch-no-security.yml 사용
docker-compose -f docker-compose-elasticsearch-no-security.yml up -d
```

**해결방법 2** (인증 정보 확인):
```properties
# application.properties
spring.elasticsearch.username=elastic
spring.elasticsearch.password=meomulm2026!
```

---

### 문제 6: 한글 주석 깨짐
```java
// ì ˜¤ì ´ë ¯¸ì ¡€ì  íšŒ  (깨진 문자)
```

**원인**: UTF-8 인코딩 문제

**해결방법**:
```bash
# IntelliJ 설정
File > Settings > Editor > File Encodings
→ Global Encoding: UTF-8
→ Project Encoding: UTF-8

# 파일 재저장
```

---

## 모니터링

### Kibana를 통한 모니터링

1. **인덱스 상태 확인**
```bash
GET /_cat/indices?v
```

2. **클러스터 상태**
```bash
GET /_cluster/health
```

3. **검색 성능 분석**
```bash
GET /accommodations/_search
{
  "profile": true,
  "query": {
    "match": {
      "accommodationName": "강남"
    }
  }
}
```

### 애플리케이션 로그

```bash
# Elasticsearch 로그
docker logs -f meomulm-elasticsearch

# Spring Boot 로그
tail -f logs/application.log
```

---

## 참고 자료

### 공식 문서
- [Elasticsearch 8.12 공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/8.12/index.html)
- [Nori 분석기 가이드](https://www.elastic.co/guide/en/elasticsearch/plugins/8.12/analysis-nori.html)
- [Spring Data Elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Elasticsearch Java Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/8.12/index.html)

### 튜토리얼
- [한글 검색 최적화 가이드](https://www.elastic.co/kr/blog/nori-korean-analyzer)
- [GeoPoint 검색](https://www.elastic.co/guide/en/elasticsearch/reference/8.12/geo-point.html)
- [Bulk API 사용법](https://www.elastic.co/guide/en/elasticsearch/reference/8.12/docs-bulk.html)

---

## 버전 정보

| 구성요소 | 버전 |
|---------|------|
| Elasticsearch | 8.12.0 |
| Kibana | 8.12.0 |
| Nori Plugin | 8.12.0 |
| Spring Boot | 3.2.1 |
| Spring Data Elasticsearch | 5.2.x |
| Elasticsearch Java Client | 8.12.0 |
| PostgreSQL | 16-alpine |
| Java | 21 |

---

## 다음 단계

1. **성능 테스트**: JMeter로 부하 테스트
2. **보안 강화**: TLS/SSL 적용 (프로덕션)
3. **모니터링 강화**: Elastic APM 도입
4. **클러스터링**: 다중 노드 구성 (고가용성)
5. **동의어 사전**: 한글 동의어 추가
6. **자동완성 개선**: Completion Suggester 적용

---

## 팁

### 개발 시 유용한 명령어

```bash
# 전체 인덱스 삭제 (주의!)
curl -X DELETE http://localhost:9200/_all

# 특정 인덱스 삭제
curl -X DELETE http://localhost:9200/accommodations

# 인덱스 매핑 확인
curl http://localhost:9200/accommodations/_mapping?pretty

# 문서 개수 확인
curl http://localhost:9200/accommodations/_count?pretty

# 모든 문서 조회 (최대 10개)
curl http://localhost:9200/accommodations/_search?pretty
```

### Kibana Dev Tools 예제

```json
# 한글 검색 테스트
POST /accommodations/_search
{
  "query": {
    "match": {
      "accommodationName": "강남 호텔"
    }
  }
}

# 위치 검색 테스트
POST /accommodations/_search
{
  "query": {
    "geo_distance": {
      "distance": "5km",
      "location": {
        "lat": 37.5665,
        "lon": 126.9780
      }
    }
  }
}
```
