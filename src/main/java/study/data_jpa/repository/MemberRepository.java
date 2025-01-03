package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsername(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 로 직접 조회
    // Member 의 id, username, Team 의 name 을 가져와서 MemberDto 생성자에 넣어서 만들어준다.
    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) " + "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

}
