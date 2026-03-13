package com.ordersystem.order.ordering.repository;

import com.ordersystem.order.member.domain.Member;
import com.ordersystem.order.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingRepository extends JpaRepository<Ordering, Long> {

    List<Ordering> findAllByMember(Member member);
}
