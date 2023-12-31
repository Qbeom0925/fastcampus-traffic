package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import java.sql.ResultSet;
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
public class MemberRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    static private final String TABLE = "Member";

    RowMapper<Member> rowMapper = (ResultSet resultSet, int rowNum) -> Member.builder()
        .id(resultSet.getLong("id"))
        .nickname(resultSet.getString("nickname"))
        .email(resultSet.getString("email"))
        .birthday(resultSet.getTimestamp("birthday").toLocalDateTime())
        .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
        .build();

    public Optional<Member> findById(Long id){
        val sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
        var param = new MapSqlParameterSource()
            .addValue("id", id);

        var member = namedParameterJdbcTemplate.queryForObject(sql, param, rowMapper);
        return Optional.ofNullable(member);
    }

    public List<Member> findAllByIdIn(List<Long> ids){
        if (ids.isEmpty())
            return List.of();
        var sql = String.format("SELECT * FROM %s WHERE id IN (:ids)", TABLE);
        var params = new MapSqlParameterSource()
            .addValue("ids", ids);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public Member save(Member member){
        if (member.getId() == null){
            return insert(member);
        }
        return update(member);
    }

    private Member insert(Member member){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
            .withTableName("Member")
            .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return Member.builder()
            .id(id)
            .email(member.getEmail())
            .nickname(member.getNickname())
            .birthday(member.getBirthday())
            .createdAt(member.getCreatedAt())
            .build();
    }

    private Member update(Member member){
        var sql = String.format("UPDATE Member SET nickname = :nickname, email = :email, birthday = :birthday WHERE id = :id", TABLE);
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(
            member);
        namedParameterJdbcTemplate.update(sql, params);
        return member;
    }
}
