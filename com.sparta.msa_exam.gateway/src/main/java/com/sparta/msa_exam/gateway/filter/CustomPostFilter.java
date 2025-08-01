package com.sparta.msa_exam.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.logging.Logger;

@Component
public class CustomPostFilter implements GlobalFilter, Ordered {

    @Value("${server.port}")
    private String serverPort;      //현재 실행 중인 서버 포트 값

    private static final Logger logger = Logger.getLogger(CustomPostFilter.class.getName());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            logger.info("Post Filter: Response status code is " + response.getStatusCode());

            // 응답 헤더에 실행 서비스 포트 추가
            URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            if (uri != null && uri.getScheme().startsWith("http")) {
                int port = uri.getPort();

                // 포트 정보가 유효한 경우 헤더에 추가
                if (port != -1) {
                    response.getHeaders().add("Server-Port", String.valueOf(port));
                }
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
