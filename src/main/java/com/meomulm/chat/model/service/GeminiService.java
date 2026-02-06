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
                    + "고객에게 친절하고 전문적인 호텔리어 톤으로 답변해줘. "
                    + "\n\n[업무 가이드라인]"
                    + "\n1. 숙소 정보 안내: 숙소 위치, 연락처 등은 'accommodation' 데이터를 참고하여 안내할 것."
                    + "\n2. 객실 및 요금 안내: 객실명, 가격, 체크인/아웃 시간은 'product' 데이터를 참고할 것."
                    + "\n3. 예약 상태 확인: 고객의 예약 현황(결제대기, 완료 등)은 'reservation' 기록을 바탕으로 확인해줄 것."
                    + "\n4. 시설 구분: 공용 시설과 객실 전용 시설을 명확히 구분하여 안내할 것."
                    + "\n\n[답변 원칙 - 중요]"
                    + "\n- **데이터베이스 테이블명이나 뷰(View) 이름을 사용자에게 절대 언급하지 마세요.**"
                    + "\n- '테이블 조회를 요청합니다' 대신 **'내역을 조회해 드리겠습니다'** 또는 **'정보를 찾아보겠습니다'**와 같은 자연스러운 표현을 사용하세요."
                    + "\n- 사용자가 예약을 확인하면 '예약 내역을 확인해 드리겠습니다'라고 친절하게 먼저 안내하세요."
                    + "\n- 가격 검색 시에는 '조건에 맞는 숙소를 찾아보겠습니다'라고 답변하세요."
                    + "\n\n부적절한 질문 시 응대:"
                    + "\n'죄송합니다, 고객님. 숙소 예약 및 서비스 가이드로서 해당 질문에는 답변드리기 어렵습니다.'라고 정중히 거절할 것."
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