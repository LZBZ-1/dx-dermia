package com.lzbz.auth.aspect;

import com.lzbz.auth.annotation.RateLimit;
import com.lzbz.auth.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterAspect {

    private final RateLimiterService rateLimiterService;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServerWebExchange exchange = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ServerWebExchange) {
                exchange = (ServerWebExchange) arg;
                break;
            }
        }

        if (exchange == null) {
            return joinPoint.proceed();
        }

        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String type = rateLimit.type();

        return rateLimiterService.isAllowed(ip, type)
                .flatMap(allowed -> {
                    if (allowed) {
                        try {
                            return (Mono<?>) joinPoint.proceed();
                        } catch (Throwable e) {
                            return Mono.error(e);
                        }
                    }
                    return Mono.just(ResponseEntity.status(429)
                            .body("Too many requests. Please try again later."));
                });
    }
}