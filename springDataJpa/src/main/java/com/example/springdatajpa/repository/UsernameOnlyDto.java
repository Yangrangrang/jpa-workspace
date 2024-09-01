package com.example.springdatajpa.repository;

public record UsernameOnlyDto(String username) {

    // 생성자의 파라미터이름으로 매칭을 시켜서 프로젝션
}
