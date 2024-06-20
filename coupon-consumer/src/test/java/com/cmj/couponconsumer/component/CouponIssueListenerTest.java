package com.cmj.couponconsumer.component;

import com.cmj.couponconsumer.TestConfig;
import com.cmj.couponcore.repository.redis.RedisRepository;
import com.cmj.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    CouponIssueListener sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisRepository redisRepository;

    @MockBean
    CouponIssueService couponIssueService;

    @BeforeEach
    void clear() {
        Collection<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            keys.forEach(key -> redisTemplate.delete(key));
        }
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리대상이 없다면 발급을 하지 않는다.")
    void issue_1() throws JsonProcessingException {
        //when
        sut.issue();
        // then
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리대상이 있다면 발급한다.")
    void issue_2() throws JsonProcessingException {
        //given
        long couponId = 1;
        long userId = 1;
        int totalIssueQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
        //when
        sut.issue();
        // then
        verify(couponIssueService, times(1)).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 요청 순서에 맞게 처리된다.")
    void issue_3() throws JsonProcessingException {
        //given
        long couponId = 1;
        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;
        int totalIssueQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId1, totalIssueQuantity);
        redisRepository.issueRequest(couponId, userId2, totalIssueQuantity);
        redisRepository.issueRequest(couponId, userId3, totalIssueQuantity);
        //when
        sut.issue();
        // then
        InOrder inOrder = inOrder(couponIssueService);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId2);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId3);
    }

}