package com.meomulm.user.model.mapper;

import com.meomulm.user.model.dto.EmailAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailAuthMapper {

    /** 인증 정보 저장 (이미 존재하면 덮어쓰기 - UPSERT) */
    void upsertEmailAuth(EmailAuth emailAuth);

    /** 이메일로 인증 정보 조회 */
    EmailAuth selectByEmail(String userEmail);

    /** 이메일 기준 인증 정보 삭제 */
    void deleteByEmail(String userEmail);
}