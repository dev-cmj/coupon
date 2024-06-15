package com.cmj.couponcore.repository.mysql;

import com.cmj.couponcore.model.CouponIssue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.cmj.couponcore.model.QCouponIssue.couponIssue;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private final JPAQueryFactory jpaQueryFactory;

//    public CouponIssue findFirstCouponIssue(long couponId, long userId) {
//        return jpaQueryFactory.selectFrom(couponIssue)
//                .where(couponIssue.couponId.eq(couponId)
//                        .and(couponIssue.userId.eq(userId)))
//                .fetchFirst();
//    }

    public Optional<CouponIssue> findFirstCouponIssue(long couponId, long userId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId)
                        .and(couponIssue.userId.eq(userId)))
                .fetchFirst());
    }


}
