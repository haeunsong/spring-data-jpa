package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository  {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        // JPQL 사용 - 객체를 대상으로 하는 쿼리
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    // Member 가 널일 수도 , 아닐 수도 있다. -> Optional
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findByUsername(String username) {
        List<Member> resultList = em.createNamedQuery("Member.findByUsername",Member.class)
                .setParameter("username", username)
                .getResultList();

        return resultList;
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
