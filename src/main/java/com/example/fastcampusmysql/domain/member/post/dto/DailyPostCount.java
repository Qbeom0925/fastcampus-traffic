package com.example.fastcampusmysql.domain.member.post.dto;

import java.time.LocalDate;

public record DailyPostCount(Long memberId, LocalDate date, Long postCount) {

}
