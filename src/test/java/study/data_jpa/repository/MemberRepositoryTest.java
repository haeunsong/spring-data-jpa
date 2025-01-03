package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    public void findByNames() {
        Member m1 = Member.builder()
                .username("haeun")
                .build();

        Member m2 = Member.builder()
                .username("freedom")
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> byNames = memberRepository.findByNames(Arrays.asList("haeun","freedom"));
        System.out.println("byNames: " + byNames);

    }
    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .team(team)
                .build();

        memberRepository.save(m1);

        List<MemberDto> memberDtoList = memberRepository.findMemberDto();
        for (MemberDto dto : memberDtoList) {
            System.out.println(dto);
        }

        // 검증
        assertThat(memberDtoList).isNotEmpty(); // DTO 리스트가 비어 있지 않아야 함
        assertThat(memberDtoList).hasSize(1);   // 리스트 크기는 1이어야 함

        MemberDto dto = memberDtoList.get(0);
        assertThat(dto.getUsername()).isEqualTo("AAA");   // username이 "AAA"인지 확인
        assertThat(dto.getTeamName()).isEqualTo("teamA"); // teamName이 "teamA"인지 확인
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("MemberA", 10);
        Member member2 = new Member("MemberB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> nameList = memberRepository.findUsernameList();
        assertThat(nameList).containsExactly("MemberA", "MemberB");
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("MemberA", 10);
        Member member2 = new Member("MemberB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("MemberA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("MemberA");
        Member member2 = new Member("MemberB");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검사
        Optional<Member> findMember1 = memberRepository.findById(member1.getId());
        Optional<Member> findMember2 = memberRepository.findById(member2.getId());
        assertThat(findMember1.get()).isEqualTo(member1);
        assertThat(findMember2.get()).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}