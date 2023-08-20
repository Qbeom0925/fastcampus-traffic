package com.example.fastcampusmysql.domain.member.post.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record DailyPostCountRequest(
    Long memberId,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate firstDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate lastDate
) {

}
