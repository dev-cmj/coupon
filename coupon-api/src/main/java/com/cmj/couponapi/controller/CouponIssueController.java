package com.cmj.couponapi.controller;

import com.cmj.couponapi.controller.dto.CouponIssueRequestDto;
import com.cmj.couponapi.controller.dto.CouponIssueResponseDto;
import com.cmj.couponapi.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto couponIssueRequestDto) {
        couponIssueRequestService.issueRequestV1(couponIssueRequestDto);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v1/issue-async")
    public CouponIssueResponseDto asyncIssueV1(@RequestBody CouponIssueRequestDto couponIssueRequestDto) {
        couponIssueRequestService.asyncIssueRequestV1(couponIssueRequestDto);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v2/issue-async")
    public CouponIssueResponseDto asyncIssueV2(@RequestBody CouponIssueRequestDto couponIssueRequestDto) {
        couponIssueRequestService.asyncIssueRequestV2(couponIssueRequestDto);
        return new CouponIssueResponseDto(true, null);
    }
}
