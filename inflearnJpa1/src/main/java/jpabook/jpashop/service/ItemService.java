package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional  // ItemService에 트랜잭션 리드온니 트루값을 했기 때문에 save는 트루이면 안되서 붙힘.
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long id, Book bookParam) {
        Item findItem = itemRepository.findOne(id);
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());

        // 아무것도 해줄 필요 없다.
        // findItem으로 가져온건 준영속성이 아닌 영속성 상태이기때문에
        // 값을 세팅한 다음에 @Transactional에 의해서 commit이 되고
        // JPA는 플러시를 날려서(영속성 컨텍스트에 있는 엔티티중에 변경된애가 뭔지 찾는다)
        // 바뀐값을 디비에 날려서 update 처버림. (변경감지에 의해 데이터를 변경하는 방법)
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
