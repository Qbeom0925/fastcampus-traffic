package com.example.fastcampusmysql.domain.member.service;

import com.example.fastcampusmysql.domain.member.dto.RegisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNickNameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    private final MemberRepository memberRepository;
    private final MemberNickNameHistoryRepository memberNickNameHistoryRepository;

    public Member create(RegisterMemberCommand command){
        var member = Member.builder()
            .nickname(command.nickname())
            .email(command.email())
            .birthday(command.birthdate())
            .build();
        return memberRepository.save(member);
    }

    public void update(Long id, String nickname){
        var member = memberRepository.findById(id).orElseThrow();
        member.changeNickname(nickname);
        memberRepository.save(member);
    }

    public void changeNickname(Long id, String nickname){
        var member = memberRepository.findById(id).orElseThrow();
        member.changeNickname(nickname);

        var savedMember = memberRepository.save(member);
        saveMemberNicknameHistory(savedMember);
    }

    private void saveMemberNicknameHistory(Member member) {
        var build = MemberNameHistory
            .builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .build();
    }


}
