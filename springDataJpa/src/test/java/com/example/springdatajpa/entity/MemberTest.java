package com.example.springdatajpa.entity;

import com.example.springdatajpa.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("member1" ,10, team1);
        Member member2 = new Member("member2" ,20, team1);
        Member member3 = new Member("member3" ,30, team2);
        Member member4 = new Member("member4" ,40, team2);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush();
        em.clear();

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member);  // @PrePersist

        Thread.sleep(100);
        member.setUsername("member2");
        
        em.flush(); //@PreUpdate
        em.clear(); 
        
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        
        //then
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
//        System.out.println("findMember.getUpdatedDate() = " + findMember.getUpdatedDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }

}