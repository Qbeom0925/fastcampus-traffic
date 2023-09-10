package com.example.fastcampusmysql.domain.member.post.repository;

import com.example.fastcampusmysql.util.PageHeader;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.entity.Post;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    static final String TABLE = "POST";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
        .id(resultSet.getLong("id"))
        .memberId(resultSet.getLong("memberId"))
        .contents(resultSet.getString("contents"))
        .createdDate(resultSet.getObject("createdDate", LocalDate.class))
        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
        .build();

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

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable){
        var params = new MapSqlParameterSource()
            .addValue("memberId", memberId)
            .addValue("size", pageable.getPageSize())
            .addValue("offset", pageable.getOffset());

        var sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY %s
                limit :size
                offset :offset
                """, TABLE, PageHeader.orderBy(pageable.getSort()));

        var posts = namedParameterJdbcTemplate.query(sql, params,ROW_MAPPER);

        var count = getCount(memberId);

        return new PageImpl<>(posts, pageable, count);
    }

    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size){
        var sql = """
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
            .addValue("memberId", memberId)
            .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThenIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size){
        var sql = """
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
            .addValue("memberId", memberId)
            .addValue("id", id)
            .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    private Long getCount(Long memberId){
        var sql = """
                SELECT count(id)
                FROM %s
                WHERE memberId = :memberId
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
            .addValue("memberId", memberId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
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
