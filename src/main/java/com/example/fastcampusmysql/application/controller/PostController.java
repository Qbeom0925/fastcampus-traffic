package com.example.fastcampusmysql.application.controller;

import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.member.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.member.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.member.post.service.PostReadService;
import com.example.fastcampusmysql.domain.member.post.service.PostWriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
