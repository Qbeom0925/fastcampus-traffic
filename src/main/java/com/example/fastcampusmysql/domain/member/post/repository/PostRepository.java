package com.example.fastcampusmysql.domain.member.post.repository;

import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.entity.Post;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    static final String TABLE = "POST";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static private RowMapper<DailyPostCount> DALY_POST_COUNT_MAPPER =  (ResultSet resultSet, int rowNum)
        -> new DailyPostCount(
        resultSet.getLong("memberId"),
        resultSet.getObject("createdDate", LocalDate.class),
        resultSet.getLong("count(id)")
    );

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        var sql = """
                SELECT createdDate, memberId, count(id)
                FROM %s
                WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                GROUP BY createdDate, memberId
                """.formatted(TABLE);
        var params = new BeanPropertySqlParameterSource(request);
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> new DailyPostCount(
            rs.getLong("memberId"),
            rs.getObject("createdDate", LocalDate.class),
            rs.getLong("count(id)")
        ));
    }

    public Post save(Post post) {
        if (post.getId() == null)
            return insert(post);
        return update(post);
    }

    private Post insert(Post post) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
            .withTableName(TABLE)
            .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        var id = jdbcInsert.executeAndReturnKey(params).longValue();

        return Post.builder()
            .id(id)
            .memberId(post.getMemberId())
            .contents(post.getContents())
            .createdDate(post.getCreatedDate())
            .createdAt(post.getCreatedAt())
            .build();
    }

    public void bulkInsert(List<Post> posts){
        var sql = """
                INSERT INTO %s (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """.formatted(TABLE);

        SqlParameterSource[] params = posts.stream()
            .map(BeanPropertySqlParameterSource::new)
            .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    private Post update(Post post) {
        var sql = String.format("""
        UPDATE %s set 
            memberId = :memberId, 
            contents = :contents, 
            createdDate = :createdDate, 
            createdAt = :createdAt, 
            likeCount = :likeCount,
            version = :version + 1 
        WHERE id = :id and version = :version
        """, TABLE);

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        var updatedCount = namedParameterJdbcTemplate.update(sql, params);
        if (updatedCount == 0) {
            throw new RuntimeException("not updated");
        }
        return post;
    }
}
