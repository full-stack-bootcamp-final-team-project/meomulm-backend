package com.meomulm.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.stripe.model.tax.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.StringReader;

/**
 * Elasticsearch 인덱스 초기화 클래스 (2026 버전)
 * 애플리케이션 시작 시 필요한 인덱스 생성 및 매핑 설정
 *
 *
 *
 * String... args - 가변 인자
 * String 배열을 받는데, 개수가 상관없거나, 모를 때 사용
 *
 * public void run(String... args) {
 *     //기능작성
 * }
 * // run 을 호출하는 방법
 * run()                   // 1. 인자 없음
 * run("hello")            // 2. 인자 1개
 * run("hello", "world")   // 3. 인자 2개
 *
 * 파라미터 개수를 자유롭게 작성 가능한 표기법
 *
 * s -> s.withJson(new StringReader(settings))
 * .withJson  - s 를 받아서 JSON 문자열을 객체로 변환
 * StringReader 문자열을 읽어서
 * settings 내부에 작성되어 있는 문자열
 * """ 띄어쓰기 엔터 모두 포함한 문자열 형태 읽음
 * """
 *
 *   String settings = """
 *                 {
 *                   "analysis": {
 *                     "analyzer": {
 *                       "nori": {
 *                         "type": "custom",
 *                         "tokenizer": "nori_tokenizer",
 *                         "filter": ["lowercase", "nori_part_of_speech"]
 *                       }
 *                     }
 *                   }
 *                 }
 *                 """;
 *  1. settings 와 같은 JSON 문자열 준비
 *  2. withJson 변환 .withJson(new StringReader(settings)
 *     JSON 문자열 -> Java 객체로 자동 변환
 *  결과 : Settings 객체가 만들어짐
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void run(String... args) throws Exception {
        createAccommodationIndex();
        createReviewIndex();
        createProductIndex();
    }

    /**
     * 숙소 인덱스 생성
     */
    private void createAccommodationIndex() {
        try {
            String indexName = "accommodations";
            
            // 인덱스 존재 여부 확인
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            // 인덱스 매핑 설정 (JSON)
            String mapping = """
                {
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
                """;

            // 인덱스 설정 (한글 분석기 nori 플러그인 사용)
            /*
            엘라스틱서치를 이용할 때 한글 표기가 되어 있는 검색 결과의 경우 한글 세팅 을 진행해야한다.

            json 형태로 작성하지 않는다면 아래와 같이 빌더를 이용해서 한글 세팅을 위한 세팅작업을 해주어야한다.

            Settings settings = Settings.builder()
                    .put( "analysis.analyzer.nori.type","custom")
                    .put( "analysis.analyzer.nori.tokenizer","nori_tokenizer")
                    .put( "analysis.analyzer.nori.filter.0","lowercase")
                    .put( "analysis.analyzer.nori.filter.1","nori_part_of_speech")
                    .build()
             json 형태로 작성했기 때문에 조금더 간결하고, 편안하게 볼 수 있으며, 속도 또한 위보다 빠름
             */
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

            // 인덱스 생성
            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("숙소 인덱스 생성 중 오류 발생", e);
        }
    }

    /**
     * 리뷰 인덱스 생성
     */
    private void createReviewIndex() {
        try {
            String indexName = "reviews";
            
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            String mapping = """
                {
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
                """;

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

            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("리뷰 인덱스 생성 중 오류 발생", e);
        }
    }

    /**
     * 상품(객실) 인덱스 생성
     */
    private void createProductIndex() {
        try {
            String indexName = "products";
            
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            String mapping = """
                {
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
                """;

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

            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("상품 인덱스 생성 중 오류 발생", e);
        }
    }
}
