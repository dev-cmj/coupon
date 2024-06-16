package com.cmj.couponapi.service;

import com.cmj.couponapi.controller.dto.CouponIssueRequestDto;
import com.cmj.couponcore.component.DistributeLockExecutor;
import com.cmj.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;


    public void issueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
        distributeLockExecutor.execute("lock_" + couponIssueRequestDto.couponId(), 10000, 10000,
                () -> couponIssueService.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId()));
        log.info("쿠폰 발급 요청 완료. userId: {}, couponId: {}", couponIssueRequestDto.userId(), couponIssueRequestDto.couponId());
    }
}
