package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderRepository {
    private final EntityManager entityManager;

    public void saveOrder(Orders order) {
        entityManager.persist(order);
    }

    public Orders findOneOrder(Long orderId) {
        return entityManager.find(Orders.class, orderId);
    }

//    public List<Orders> findAllOrders(OrderSearch orderSearch) {

//    }


}