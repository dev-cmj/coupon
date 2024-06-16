package com.cmj.couponcore.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {

    private final RedissonClient redissonClient;

    public void execute(String lockName, long waitMilliSecond, long leaseMilliSecond, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                throw new IllegalStateException("Lock을 획득하지 못했습니다. lockName: %s".formatted(lockName));
            }
            runnable.run();
        } catch (InterruptedException e) {
            log.error("Lock 획득 중 문제가 발생했습니다. lockName: %s".formatted(lockName), e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
