package com.example.fastcampusmysql.domain.member.dto;

public record MemberDto(
    Long id,
    String email,
    String nickname,
    String birthdate
) {

}
