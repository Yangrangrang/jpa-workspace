package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
