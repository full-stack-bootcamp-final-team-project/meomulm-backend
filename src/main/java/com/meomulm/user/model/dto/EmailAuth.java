package com.meomulm.user.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EmailAuth {

    private String userEmail;  // 유저 이메일
    private String authCode;    // 인증코드 6자리
    private LocalDateTime expireTime;  // 만료 시각
    private LocalDateTime createdAt;   // 생성 시각
}