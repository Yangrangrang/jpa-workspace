package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 규칙이 있다 클래스 이름을 정의할 떄,
 * MemberRepository 에서 사용하는 걸 만드려면 MemberRepositoryImpl 로
 * 앞에 'MemberRepository' 부분을 맞춰주고 + Impl 을 해줘야
 * spring Data JPA 가 알아서 함수 콜을 했을 때 구현체로 해당 인터페이스를 상속한 메서드를 호출해준다.
 */
@RequiredArgsConstructor    // final 이면
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
