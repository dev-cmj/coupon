package com.cmj.couponcore.service;


import com.cmj.couponcore.exception.CouponIssueException;
import com.cmj.couponcore.model.Coupon;
import com.cmj.couponcore.model.CouponIssue;
import com.cmj.couponcore.model.event.CouponIssueCompleteEvent;
import com.cmj.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.cmj.couponcore.repository.mysql.CouponIssueRepository;
import com.cmj.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;

    public void issue(long couponId, long userId) {
            Coupon coupon = findCouponWithLock(couponId);
            coupon.issue();
            saveCouponIssue(couponId, userId);
            publishCouponEvent(coupon);
    }

    public Coupon findCouponWithLock(long couponId) {
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
    }

    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
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

    private void publishCouponEvent(Coupon coupon) {
        if (coupon.isIssueComplete()) {
            applicationEventPublisher.publishEvent(new CouponIssueCompleteEvent(coupon.getId()));
        }
    }

}
