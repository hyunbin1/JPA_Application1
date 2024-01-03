package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.service.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    public List<Orders> findAllOrders(OrderSearch orderSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Orders> cq = cb.createQuery(Orders.class);
        Root<Orders> o = cq.from(Orders.class);
        Join<Orders, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate orderStatus = cb.equal(o.get("orderStatus"),
                    orderSearch.getOrderStatus());
            criteria.add(orderStatus);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate username =
                    cb.like(m.<String>get("username"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(username);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Orders> query = entityManager.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Orders> findAllWithMemberDelivery() {
        return entityManager.createQuery(
                "select o from Orders o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Orders.class
                ).getResultList();

    }
}
