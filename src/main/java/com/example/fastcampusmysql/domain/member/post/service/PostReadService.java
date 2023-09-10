package com.example.fastcampusmysql.domain.member.post.service;

import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.entity.Post;
import com.example.fastcampusmysql.domain.member.post.repository.PostRepository;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        return postRepository.groupByCreatedDate(request);
    }

    public Page<Post> getPosts(Long memberId, Pageable pageRequest) {
        return postRepository.findAllByMemberId(memberId, pageRequest);
    }

    public PageCursor<Post> getPosts(Long memberId, CursorRequest cursorRequest) {
        List<Post> posts = findAllBy(memberId, cursorRequest);
        var nextKey = posts.stream()
            .mapToLong(Post::getId)
            .min()
            .orElse(CursorRequest.NONE);
        return new PageCursor<>(cursorRequest.next(nextKey),posts);

    }

    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()){
            return postRepository.findAllByLessThenIdAndMemberIdAndOrderByIdDesc(
                cursorRequest.key(), memberId, cursorRequest.size());
        }else{
            return postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
        }
    }
}
