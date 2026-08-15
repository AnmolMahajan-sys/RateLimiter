package com.anmol.distrilimit;

import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.*;

@Component
public class RateLimitFilter implements Filter {
    private static final int CAPACITY=5;
    public static final double REFILL_RATE_PER_SECOND=1.0;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest request,ServletResponse response, FilterChain chain) throws IOException, ServletException{
        String clientId=request.getRemoteAddr();

        RedisTokenBucket bucket=new RedisTokenBucket(redisTemplate,clientId,CAPACITY,REFILL_RATE_PER_SECOND);

        if(!bucket.tryConsume()){
            response.getWriter().write("Too many requests!");
            return;
        }
        chain.doFilter(request,response);
    }
}
