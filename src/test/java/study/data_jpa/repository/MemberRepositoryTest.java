package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @PersistenceContext
    private EntityManager em;

    // EntityGraph 알아보기 : 연관된 엔티티들을 sql 한번에 조회하는 방법
    // member -> team 은 지연로딩으로 설정되어있기에, getTeam().getName() 을 할때마다
    // 팀 정보를 얻어오기 위한 쿼리가 한 번 더 실행된다. (N+1문제 발생)
    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();
//when
        List<Member> members = memberRepository.findAll();
//then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }

    @Test
    public void paging() throws Exception {
        // given
        Team team = new Team("meyame");
        teamRepository.save(team);
        Member m = Member.builder()
                        .username("멤버1")
                        .age(23)
                        .team(team)
                        .build();

        memberRepository.save(m);
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // 한 페이지당 3개씩 데이터를 가져온다. 만약 데이터가 10개라면, 총 4페이지.

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age,pageRequest);
        // 엔티티는 무조건 밖에 노출되면 안된다. 그니까 컨트롤러에서 절대 엔티티를 반환하면 안된다.
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(),null));
        System.out.println("toMap = " + toMap);

        // then
        List<Member> content = page.getContent(); // 조회된 데이터
        assertThat(content.size()).isEqualTo(3);
        //assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        //assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

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