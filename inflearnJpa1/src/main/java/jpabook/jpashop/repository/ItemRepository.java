package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // Id값이 없다는건 새로 생성한 객체
            em.persist(item);       // persist 로 새로 등록
        } else {
            em.merge(item);         // 이미 등록되있는 걸 어디에서 가져온거, 즉 여기서 save는 update라고 생각하면 됨.(진짜 업데이트는 아니지만 비슷하다고 생각)
            // merge는 병합 (준영속상태의 엔티티를 영속 상태로 변경 할 때 사용)
            // 모든 데이터를 바꿔치기 해버림 (간단하게 정리하면, ItemService에 있는 updateItem메소드를 JPA에서 알아서 해주는것)
            // 차이가 있음. (반환을 해줌)
            // 기존에 파라미터로 넘어온 객체가 변하지는 않는다.
            // 병합은 조심해야하는게 있다.
            // 변경감지 기능을 사용하면 원하는 속성을 사용하지만, 병합을 사용하게 되면 모든 속성을 갈아 엎는다.
            // 실무에서는 위험한, 병합시 값이 없으면 null로 없데이트를 해버림.
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
