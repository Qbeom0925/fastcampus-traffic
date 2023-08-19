package com.example.fastcampusmysql.domain.member.service;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.MemberNicknameHistoryDto;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNickNameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberReadService {

    final private MemberRepository memberRepository;
    private final MemberNickNameHistoryRepository memberNickNameHistoryRepository;

    public MemberDto getMember(Long id){
        Member member = memberRepository.findById(id).orElseThrow();
        return toDto(member);
    }

    public List<MemberDto> getMembers(List<Long> ids){
        List<Member> allByIdIn = memberRepository.findAllByIdIn(ids);
        return allByIdIn.stream()
            .map(this::toDto)
            .toList();
    }

    public List<MemberNicknameHistoryDto> getMemberNicknameHistory(Long memberId){
        return memberNickNameHistoryRepository.findAllByMemberId(memberId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    public MemberDto toDto(Member member){
        return new MemberDto(
            member.getId(),
            member.getEmail(),
            member.getNickname(),
            member.getBirthday().toString()
        );
    }

    private MemberNicknameHistoryDto toDto(MemberNameHistory history){
        return new MemberNicknameHistoryDto(
            history.getId(),
            history.getMemberId(),
            history.getNickname(),
            history.getCreatedAt()
        );
    }

}
