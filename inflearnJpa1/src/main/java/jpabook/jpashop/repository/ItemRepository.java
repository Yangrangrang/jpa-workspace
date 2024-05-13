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
