package com.cmj.couponcore.service;


import com.cmj.couponcore.exception.CouponIssueException;
import com.cmj.couponcore.model.Coupon;
import com.cmj.couponcore.repository.redis.RedisRepository;
import com.cmj.couponcore.repository.redis.dto.CouponRedisEntity;
import com.cmj.couponcore.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmj.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;
import static com.cmj.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantity(CouponRedisEntity coupon, long userId) {
        if (!availableUserIssueQuantity(coupon.id(), userId)) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "발급 가능한 수량을 초과합니다. couponId : %s, userId: %s".formatted(coupon.id(), userId));
        }
        if (!availableTotalIssueQuantity(coupon.totalQuantity(), coupon.id())) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과합니다. couponId : %s, userId : %s".formatted(coupon.id(), userId));
        }
    }

    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if(totalQuantity == null) {
            return true;
        }

        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}
