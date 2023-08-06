package com.example.fastcampusmysql.domain.member;


import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.util.MemberFixtureFactory;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberTest {

    @DisplayName("회원은 닉네임을 변경할 수 있다.")
    @Test
    public void testChangeName() throws Exception{
        var member = MemberFixtureFactory.create();
        var expected = "닉네임";

        member.changeNickname(expected);

        Assertions.assertEquals(member.getNickname(), expected);
    }

    @DisplayName("회원은 닉네임은 10자를 초과할 수 없다.")
    @Test
    public void testNickNameMaxLength() throws Exception{
        var member = MemberFixtureFactory.create();
        var expected = "1234567890";

        member.changeNickname(expected);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            member.changeNickname("12345678901");
        });
    }




}
