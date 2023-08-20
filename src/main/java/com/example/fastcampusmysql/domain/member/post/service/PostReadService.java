package com.example.fastcampusmysql.domain.member.post.service;

import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        return postRepository.groupByCreatedDate(request);
    }
}
