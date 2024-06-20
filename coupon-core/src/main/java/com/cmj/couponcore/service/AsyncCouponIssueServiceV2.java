package com.cmj.couponcore.service;

import com.cmj.couponcore.component.DistributeLockExecutor;
import com.cmj.couponcore.exception.CouponIssueException;
import com.cmj.couponcore.repository.redis.RedisRepository;
import com.cmj.couponcore.repository.redis.dto.CouponIssueRequest;
import com.cmj.couponcore.repository.redis.dto.CouponRedisEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmj.couponcore.exception.ErrorCode.FAIL_COUPON_ISSUE_REQUEST;
import static com.cmj.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.cmj.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {

    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId);
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId, coupon.totalQuantity());
    }

    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity) {
        if(totalIssueQuantity == null) totalIssueQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }
}
