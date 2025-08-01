package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductInfo;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.ProductInOrder;
import com.sparta.msa_exam.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;

    /*Order 인스턴스 생성 및 DB 저장 후 응답 Dto 반환*/
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Order order = new Order();
        checkProductList(requestDto, order);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getOrder_id(), requestDto.getProduct_ids());
    }

    /*상품 목록 조회 API 호출, product_ids의 모든 값이 상품 목록에 존재하는지 확인*/
    @CircuitBreaker(name="orderService", fallbackMethod = "fallbackCheckProductList")
    public void checkProductList(OrderRequestDto requestDto, Order order) {
        List<ProductInfo> productList = productClient.getProducts();
        Map<Long, ProductInfo> productMap = productList.stream().collect(Collectors.toMap(ProductInfo::getProduct_id, Function.identity()));

        List<ProductInOrder> product_ids = new ArrayList<>();   //요청 받은 상품 목록에서 존재하는 것만 저장
        for(Long product_id : requestDto.getProduct_ids()){
            if(productMap.containsKey(product_id)){             //전체 상품 목록 중 product_id 값이 있는지 확인
                ProductInOrder product = new ProductInOrder(order, product_id);
                product_ids.add(product);                       //유효한 상품 리스트에 저장
            } else{
                throw new IllegalArgumentException(product_id + "번 상품을 찾을 수 없습니다.");
            }
        }
        order.setProduct_ids(product_ids);
    }

    public void fallbackCheckProductList(Throwable throwable) {
        if(throwable instanceof IllegalArgumentException){
            log.info("존재하지 않는 상품 주문 시도");
            throw (IllegalArgumentException) throwable;
        } else{
            log.info("상품 서비스 호출 실패");
            throw new RuntimeException(throwable.getMessage());
        }
    }
}