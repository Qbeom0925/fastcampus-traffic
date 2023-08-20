package com.example.fastcampusmysql.domain.post;

import com.example.fastcampusmysql.domain.member.post.entity.Post;
import com.example.fastcampusmysql.domain.member.post.repository.PostRepository;
import com.example.fastcampusmysql.util.PostFixtureFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@SpringBootTest
public class PostBulkInsertTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void bulkInsert() {
        EasyRandom easyRandom = PostFixtureFactory.get(3L,
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 2, 1)
        );
        var stopWatch = new StopWatch();

        int _1만 = 10000;
        List<Post> posts = IntStream.range(0, _1만 * 100)
            .parallel()
            .mapToObj(i -> easyRandom.nextObject(Post.class))
            .toList();

        stopWatch.stop();
        System.out.println("객체 생성 시간 : "+stopWatch.getTotalTimeSeconds());

        stopWatch.start();
        postRepository.bulkInsert(posts);
        stopWatch.stop();

        System.out.println("총 걸린 시간 : "+stopWatch.getTotalTimeSeconds());
    }

}
