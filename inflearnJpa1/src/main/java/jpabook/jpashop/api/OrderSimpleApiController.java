package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simpleQueryRepository.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simpleQueryRepository.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * v1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        log.info("ordersV1z>>>>>");

        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();    // getMember() 만 하면 프록시 객체 .getName까지하면 Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * v2. 엔티티를 조회해서 DTO로 변환 (fetch join 사용 X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     * - 강의에서는 쿼리 5번 호출 (initDB 주문 2개) order -> member1 -> delivery1 -> member2 -> delivery2
     * - 실제 api호출시 쿼리 7번 호출 확인 order -> member1 -> delivery1 -> deliveryId로 order찾는 쿼리1 -> member2 -> delivery2 -> deliveryId로 order쿼리2
     * - 이유를 검색했을 때, 하이버네이트 버전이 업그레이드 되면서 발생하는 문제로 예상, hibernate6으로 했을 때도 동일 현상 발생 (해결사항을 찾지 못함...)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        log.info("ordersV2>>>>>");

        // ORDER 2개
        // N + 1 -> 1 + 회원N + 배송N (첫번째 쿼리의 결과로 n번만큼 쿼리가 추가 실행되는 게 n + 1
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * v3. 엔티티를 조회해서 DTO로 변환 (fetch join 사용 O)
     * - fetch join 쿼리 1번 호출
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * v4. 엔티티를 조회해서 DTO로 변환 직접 쿼리 작성
     * select 절에 내가 원하는 데이터만 가져옴.
     * - 로직을 재활용 할 수가 없다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화
        }
    }

}
