package com.meomulm.chat.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}") // 설정 파일에서 키를 쏙 가져와요!
    private String apiKey;

    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public String getGeminiResponse(String userMessage, String dbData) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. 시스템 지시어와 사용자 메시지 결합
            // 기존 systemInstruction을 아래 내용으로 교체하세요!
            String systemInstruction = "너는 숙소 예약 플랫폼 '머묾'의 전문 AI 상담사야. "
                    + "제공된 데이터베이스 구조를 바탕으로 고객에게 친절하고 전문적인 호텔리어 톤으로 답변해줘. "
                    + "\n\n[업무 가이드라인]"
                    + "\n1. 'accommodation' 테이블: 숙소 주소, 전화번호, 위치(위도/경도) 정보 제공 시 참고."
                    + "\n2. 'product' 테이블: 객실 이름, 가격, 체크인/아웃 시간 정보 제공 시 참고."
                    + "\n3. 'reservation' 테이블: 사용자의 예약 상태(결제대기, 결제완료, 이용완료, 취소) 확인 시 참고."
                    + "\n4. 시설 정보: 'accommodation_facility'(공용시설), 'product_facility'(객실시설) 정보를 구분해서 안내."
                    + "\n\n[데이터 요청 규칙]"
                    + "\n- 사용자가 '내 예약 확인해줘'라고 하면 reservation 테이블 조회를 요청할 것."
                    + "\n- '싼 방 있어?'와 같은 검색 요청 시 vw_searchpage_accommodation 뷰 조회를 요청할 것."
                    + "\n\n만약 서비스와 관련 없는 부적절한 질문을 받으면 "
                    + "'죄송합니다, 고객님. 숙소 예약 및 서비스 가이드로서 해당 질문에는 답변드리기 어렵습니다.'라고 정중히 거절해줘."
                    + "\n\n사용자 질문: ";

            Map<String, Object> requestBody = new HashMap<>();

            Map<String, String> textPart = new HashMap<>();
            textPart.put("text", systemInstruction + userMessage);

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("parts", List.of(textPart));

            requestBody.put("contents", List.of(contentMap));

            // 4. 내용 전달
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 5. 주소 조립 및 호출
            String url =  apiUrl + "?key=" + apiKey;
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            // 6. 결과 파싱
            if (response != null && response.containsKey("candidates")) {
                List candidates = (List) response.get("candidates");
                Map firstCandidate = (Map) candidates.get(0);
                Map resContent = (Map) firstCandidate.get("content");
                List parts = (List) resContent.get("parts");
                Map firstPart = (Map) parts.get(0);
                return (String) firstPart.get("text");
            }
            return "AI가 응답을 보내지 않았어요.";
        } catch (Exception e) {
            System.err.println("=== Gemini 에러 발생 ===");
            e.printStackTrace();
            return "죄송해요, AI와 연결되지 않았어요. 관리자에게 문의해주세요.";
        }
    }
}