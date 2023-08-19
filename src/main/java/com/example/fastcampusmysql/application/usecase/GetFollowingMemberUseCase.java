package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.service.MemberReadService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetFollowingMemberUseCase {

    private final MemberReadService memberReadService;
    private final FollowReadService followReadService;

    public List<MemberDto> execute(Long memberId){
        /*
           1. fromMemberId = memberId -> follow list
         */
        List<Follow> followings = followReadService.getFollowings(memberId);
        List<Long> longs = followings.stream().map(Follow::getToMemberId).toList();
        return memberReadService.getMembers(longs);
    }

}
