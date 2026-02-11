package com.meomulm.accommodation.model.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;           // ElasticSearch 와 통신하는 메인 클라이언트
import co.elastic.clients.elasticsearch._types.SortOrder;              // 오름차순 내림차순 enum
import co.elastic.clients.elasticsearch._types.query_dsl.*;            // 쿼리 작성에 필요한 모든 DSL 클래스들
import co.elastic.clients.elasticsearch.core.SearchRequest;            // 검색 요청 객체
import co.elastic.clients.elasticsearch.core.SearchResponse;           // 검색 응답 객체
import co.elastic.clients.elasticsearch.core.search.Hit;               // 검색 결과의 개별 항목(문서)
import co.elastic.clients.json.JsonData;                               // JSON 데이터 래퍼 클래스
import com.meomulm.accommodation.model.document.AccommodationDocument; // 숙소 정보를 담는 문서
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest; // 검색 요청 DTO
import lombok.RequiredArgsConstructor;                                 // final 필드에 대한 생성자 자동 생성
import lombok.extern.slf4j.Slf4j;                                      // 로깅을 위한 어노테이션
import org.springframework.stereotype.Service;                         // 스프링 서비스 컴포넌트 어노테이션

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;                                    // 스트림 수집을 위한 유틸리티

/**
 * Elasticsearch 검색 서비스 (2026 버전) - 코드 작성을 축소하여 메서드를 사용한다는 의미
 * Elasticsearch Java API Client 8.x 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationElasticsearchService {
    // @Autowired 처럼 엘라스틱 서치 클라이언트를 사용하겠다. 객체를 생성한 것과 같다
    // public ElasticsearchClient e = new ElasticsearchClient();
    // 의존성 주입됨 = 이 클래스 문서에서 ElasticsearchClient 사용할 수 있도록 생성자를 만들어 주세요!
    private final ElasticsearchClient elasticsearchClient;


    /**
     * 통합 검색 (키워드, 위치, 필터링)
     * 검색 요청을 받아 숙소 문서 리스트를 반환하는 메서드
     */
    public List<AccommodationDocument> searchAccommodations(SearchAccommodationRequest request) {
        try { // 예외 처리 시작
            // 쿼리 빌더 여러 조건을 조합하기 위해 생성              쿼리 조건 작성 시작! 쿼리 작성을 모두 다 boolQuery에 담아놓겠다.
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            /*
            boolQuery
                .must(...)      // 반드시 있어야하는 쿼리 작성 AND
                .should(...)    // 하나라도 있으면 됨          OR
                .mustNot(...)   // 절대 없어야 함              NOT
                .filter(...)    // 필터링만                    서치에서 집계하는 점수 영향 XXX
             */

            // 1. 키워드 검색 (숙소명, 주소)
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                //  should :  숙소명 또는 주소에 키워드가 존재하면 OK
                //                          match : 텍스트 검색      field : 숙소명 필드에서 검색    query : 검색어              boost : 중요도 2점짜리 숙소명 매치가 제일 중요!
                boolQuery.should(s -> s.match(m -> m.field("accommodationName").query(request.getKeyword()).boost(2.0f)));
                //                                                                                                              가중치 기본점수 1점
                boolQuery.should(s -> s.match(m -> m.field("accommodationAddress").query(request.getKeyword())));
                // should 조건 중 최소 1개는 매치되어야한다.
                // 숙소명이나 주소 중 하나는 무조건 존재해야지 있는 index로 인정!
                // minimum should 개수 이상이 되면 안됨 0 ~ should 개수 까지
                boolQuery.minimumShouldMatch("1"); // should 이외 다른 것들도 최대 최소 조건 다르게 세팅가능
            }

            // 2. 위치 기반 검색 (반경 5km)
            if (request.getLatitude() != null && request.getLongitude() != null) { //위도 경도가 있으면!
                //          점수에 영향을 주지 않고 필터링만 하겠다.
                boolQuery.filter(f -> f
                        // 지리적 거리 쿼리
                    .geoDistance(g -> g
                            // 위치 필드에서
                        .field("location")
                            // 반경 5km
                        .distance("5km")
                            // 중심점 위치
                        .location(l -> l
                             // 위도 경도로 지정
                            .latlon(ll -> ll
                                // 위도
                                .lat(request.getLatitude())
                                // 경도
                                .lon(request.getLongitude())
                            )
                        )
                    )
                );
            }

            // 3. 숙소 타입 필터
            if (request.getAccommodationType() != null && !request.getAccommodationType().isEmpty()) {
                // 점수에 영향을 주지 않는 필터링 조건 추가
                boolQuery.filter(f -> f
                    // term 쿼리 : 정확한 값 매칭
                    .term(t -> t
                        // 숙소 타입 필드
                        .field("accommodationType")
                        // 필터링할 타입 값
                        .value(request.getAccommodationType())
                    )
                );
            }

            // 4. 가격 범위 필터
            // 최대 최소 가격중 하나라도 있으면
            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                // 범위 쿼리 빌더 생성 최소가격 필드 지정
                RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field("minPrice");
                // 최소 가격이 있으면
                if (request.getMinPrice() != null) {
                    // gte 이상 (greater than or equal)
                    rangeBuilder.gte(JsonData.of(request.getMinPrice()));
                }
                // 최대 가격이 있으면
                if (request.getMaxPrice() != null) {
                    // lte : 이하 (less than or equal)
                    rangeBuilder.lte(JsonData.of(request.getMaxPrice()));
                }
                // 범위 쿼리를 필터에 추가
                boolQuery.filter(f -> f.range(rangeBuilder.build()));
            }

            // 5. 평점 필터
            if (request.getMinRating() != null) {
                boolQuery.filter(f -> f
                    .range(r -> r
                        .field("averageRating")
                            .gte(JsonData.of(request.getMinRating()))
                    )
                );
            }

            // 검색 요청 빌드
            SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("accommodations")
                .query(q -> q.bool(boolQuery.build()));

            // 정렬
            if (request.getSortBy() != null) {
                switch (request.getSortBy()) {
                    case "price_asc":
                        searchBuilder.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Asc)));
                        break;
                    case "price_desc":
                        searchBuilder.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Desc)));
                        break;
                    case "rating":
                        searchBuilder.sort(s -> s.field(f -> f.field("averageRating").order(SortOrder.Desc)));
                        break;
                    case "review_count":
                        searchBuilder.sort(s -> s.field(f -> f.field("reviewCount").order(SortOrder.Desc)));
                        break;
                }
            }

            // 페이징
            int from = request.getPage() != null ? (request.getPage() - 1) * 20 : 0;
            searchBuilder.from(from).size(20);

            // 검색 실행
            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchBuilder.build(),
                AccommodationDocument.class
            );

            // 결과 변환
            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Elasticsearch 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 위치 기반 검색 (반경 5km)
     */
    public List<AccommodationDocument> searchByLocation(Double latitude, Double longitude) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .geoDistance(g -> g
                        .field("location")
                        .distance("5km")
                        .location(l -> l
                            .latlon(ll -> ll
                                .lat(latitude)
                                .lon(longitude)
                            )
                        )
                    )
                )
                .sort(sort -> sort
                    .geoDistance(g -> g
                        .field("location")
                        .location(l -> l
                            .latlon(ll -> ll
                                .lat(latitude)
                                .lon(longitude)
                            )
                        )
                        .order(SortOrder.Asc)
                    )
                )
                .size(50)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("위치 기반 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 키워드 자동완성
     */
    public List<String> autocomplete(String prefix) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .multiMatch(m -> m
                        .fields("accommodationName", "accommodationAddress")
                        .query(prefix)
                        .type(TextQueryType.PhrasePrefix)
                    )
                )
                .size(10)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .map(AccommodationDocument::getAccommodationName)
                .distinct()
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("자동완성 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 인기 숙소 조회 (지역별 가격 낮은 순)
     */
    public List<AccommodationDocument> getPopularByAddress(String address) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .match(m -> m
                        .field("accommodationAddress")
                        .query(address)
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("minPrice")
                        .order(SortOrder.Asc)
                    )
                )
                .size(12)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("인기 숙소 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }
}
