package com.cmj.couponcore.service;

import com.cmj.couponcore.component.DistributeLockExecutor;
import com.cmj.couponcore.exception.CouponIssueException;
import com.cmj.couponcore.model.Coupon;
import com.cmj.couponcore.repository.redis.RedisRepository;
import com.cmj.couponcore.repository.redis.dto.CouponIssueRequest;
import com.cmj.couponcore.util.CouponRedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmj.couponcore.exception.ErrorCode.*;
import static com.cmj.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.cmj.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        if(!coupon.availableIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 기간이 아닙니다. couponId: %s, userId: %s".formatted(couponId, userId));
        }

        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
            if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
                throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,
                        "발급 가능한 수량을 초과했습니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }

            if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
                throw new CouponIssueException(DUPLICATED_COUPON_ISSUE,
                        "이미 발급 요청이 처리됐습니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }

            issueRequest(couponId, userId);
        });
    }

    private void issueRequest(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest);

            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(issueRequest));
        }
    }
}
