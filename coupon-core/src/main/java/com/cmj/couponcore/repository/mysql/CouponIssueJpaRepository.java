package com.cmj.couponcore.repository.mysql;

import com.cmj.couponcore.model.Coupon;
import com.cmj.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
