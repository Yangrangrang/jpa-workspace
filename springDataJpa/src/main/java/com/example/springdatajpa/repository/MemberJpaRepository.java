package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
public class MemberJpaRepository {

    @PersistenceContext // 이 어노테이션을 사용하면 spring container 가 jpa 에 있는 영속성컨텍스트라 불리는 EntityManager 를 불렁옴
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
