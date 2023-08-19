package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNameHistory;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberNickNameHistoryRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    static private final String TABLE = "MemberNicknameHistory";

    static final RowMapper<MemberNameHistory> rowMapper = (ResultSet resultSet, int rowNum) -> MemberNameHistory.builder()
        .id(resultSet.getLong("id"))
        .memberId(resultSet.getLong("memberId"))
        .nickname(resultSet.getString("nickname"))
        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
        .build();

    public List<MemberNameHistory> findAllByMemberId(Long memberId){
        val sql = "SELECT * FROM " + TABLE + " WHERE memberId = :memberId";
        val params = new MapSqlParameterSource()
            .addValue("memberId", memberId);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public MemberNameHistory save(MemberNameHistory history){
        if (history.getId() == null){
            return insert(history);
        }
        throw new UnsupportedOperationException("갱신을 지원하지 않습니다.");
    }

    private MemberNameHistory insert(MemberNameHistory history){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
            .withTableName(TABLE)
            .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(history);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return MemberNameHistory.builder()
            .id(id)
            .memberId(history.getMemberId())
            .nickname(history.getNickname())
            .createdAt(history.getCreatedAt())
            .build();
    }

}
