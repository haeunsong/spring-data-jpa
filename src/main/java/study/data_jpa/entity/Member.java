package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED) // JPA쓸 때 기본 생성자가 필수인데, 이 기본 생성자를 private 으로 하면 안되고 protected 까지 열어둬야한다.
@ToString(of={"id","username","age"}) // 여기에 team 은 안하는게 좋다. 무한루프 가능성이 있다. - 가급적 내부 필드만(연관관계 없는 필드만)
public class Member {

    @Id
    @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id") // team 테이블의 PK 참조하여 team_id 컬럼 생성
    private Team team;

    public Member(String username) {
        this(username, 0);
    }
    public Member(String username, int age) {
        this(username, age, null);
    }
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team; // member 의 team 필드에 새로운 team 을 설정한다.
        team.getMembers().add(this); // 해당 팀의 members 리스트에 현재 member 를 추가한다.
    }
}
