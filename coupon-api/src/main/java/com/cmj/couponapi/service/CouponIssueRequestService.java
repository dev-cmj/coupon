package com.cmj.couponapi.service;

import com.cmj.couponapi.controller.dto.CouponIssueRequestDto;
import com.cmj.couponcore.component.DistributeLockExecutor;
import com.cmj.couponcore.service.AsyncCouponIssueServiceV1;
import com.cmj.couponcore.service.AsyncCouponIssueServiceV2;
import com.cmj.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;


    public void issueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
        couponIssueService.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
        log.info("쿠폰 발급 요청 완료. userId: {}, couponId: {}", couponIssueRequestDto.userId(), couponIssueRequestDto.couponId());
    }

    public void asyncIssueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
        asyncCouponIssueServiceV1.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
    }

    public void asyncIssueRequestV2(CouponIssueRequestDto couponIssueRequestDto) {
        asyncCouponIssueServiceV2.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
    }


}
