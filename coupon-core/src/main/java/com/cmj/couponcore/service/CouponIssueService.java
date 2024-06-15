package com.cmj.couponcore.service;


import com.cmj.couponcore.exception.CouponIssueException;
import com.cmj.couponcore.model.Coupon;
import com.cmj.couponcore.model.CouponIssue;
import com.cmj.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.cmj.couponcore.repository.mysql.CouponIssueRepository;
import com.cmj.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cmj.couponcore.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.cmj.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;

@RequiredArgsConstructor
@Service
@Transactional
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    public void issue(long couponId, long userId) {
        Coupon coupon = couponJpaRepository.findById(couponId).orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
        coupon.issue();

        saveCouponIssue(couponId, userId);
    }

    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId, userId);
        return couponIssueJpaRepository.save(CouponIssue.builder().couponId(couponId).userId(userId).build());
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        if (couponIssueRepository.findFirstCouponIssue(couponId, userId).isPresent()) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "이미 발급된 쿠폰입니다. userId: %s, couponId: %s".formatted(userId, couponId));
        }
    }

}
