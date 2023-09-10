package com.example.fastcampusmysql.application.controller;

import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.member.post.entity.Post;
import com.example.fastcampusmysql.domain.member.post.service.PostReadService;
import com.example.fastcampusmysql.domain.member.post.service.PostWriteService;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostWriteService postWriteService;
    final private PostReadService postReadService;

    @PostMapping("")
    public Long create(PostCommand postCommand){
        return postWriteService.create(postCommand);
    }

    @GetMapping("/dailt-post-counts")
    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request){
        return postReadService.getDailyPostCount(request);
    }

    @GetMapping("/members/{memberId}")
    public Page<Post> getPosts(
        @PathVariable Long memberId,
        Pageable pageable
    ){
        return postReadService.getPosts(memberId, pageable);
    }

    @GetMapping("/members/{memberId}/cursor")
    public PageCursor<Post> getPosts(
        @PathVariable Long memberId,
        CursorRequest cursorRequest
    ){
        return postReadService.getPosts(memberId,cursorRequest);
    }

}
