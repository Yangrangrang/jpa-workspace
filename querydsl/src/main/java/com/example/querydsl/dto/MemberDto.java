package com.example.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection    // Dto 도 Qclass 만듬.
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
