package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * v1. 엔티티 직접 노출
     * orderItem , item 관계를 직접 초기화하면 Hibernate5Module 설정에 의해 엔티티를 JSON으로 생성
     */
    @GetMapping("v1/orders")
    public List<Order> ordersV1() {
        log.info(">>>>>>>> v1/orders");

        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
//            for (OrderItem orderItem : orderItems) {
//                orderItem.getItem().getName();
//            }
            // 람다로
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * v2. DTO 로 변환
     */
    @GetMapping("v2/orders")
    public List<OrderDto> ordersV2() {
        log.info(">>>>>>>> v2/orders");

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto (Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();     // 이렇게만 하면 엔티티 이기때문에 결과가 null로 나옴. 위에 stream으로 반복문을 돌려줘야 원하는 값을 얻음.
//            이렇게 하면 OrderItem 은 엔티티로 노출이 되기 때문에 OrderItemDto 로 감싸줘야함.

            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;    // 상품명
        private int orderPrice;     // 주문가격
        private int count;          // 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    /**
     * v3. fetch join 사용
     * 일대다 패치 조인에서는 페이징을 하면 안된다. (강의 패치조인 최적화 확인)
     * 강의에서는 v3으로 실행을 할 경우, 결과가 4개가 나오는데 이유는 findAllWithItem 메소드에서 orderItem 이랑 패치 조인을 하기 때문
     * 해서 강의에서는 해당 메소드에서 select 문에 distinct 를 작성해주는데
     * 강의 듣는 시점에서 확인 해봤을 때 distinct 를 작성하지 않아도 결과값이 2개로 나오는 걸 확인
     * 스프링부트3, 하이버네이트6버전 사용 시 자동으로 distinct 가 적용되었기 때문으로 확인
     * (참고 https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#hql-distinct)
     */
    @GetMapping("v3/orders")
    public List<OrderDto> ordersV3() {
        log.info(">>>>>>>> v3/orders");

        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order + \" id\" + order.getId() = " + order + " id" + order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * v3.1 페이징 한계 돌파
     */
    @GetMapping("v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        log.info(">>>>>>>> v3.1/orders");

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
}
