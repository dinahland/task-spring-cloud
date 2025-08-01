package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductInfo;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.ProductInOrderRequestDto;
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
    @CircuitBreaker(name="orderService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Order order = new Order();
        checkProductList(requestDto, order);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getOrder_id(), requestDto.getProduct_ids());
    }

    /*주문과 상품의 유효성 검사 후 상품 추가해서 반환*/
    @Transactional
    @CircuitBreaker(name="orderService", fallbackMethod = "fallbackAddProduct")
    public OrderResponseDto addProduct(Long orderId, ProductInOrderRequestDto requestDto) {
        // 주문이 존재하는지 확인
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException((orderId + "번 주문을 찾을 수 없습니다.")));
        if(checkProduct(requestDto.getProduct_id())){  //상품이 존재하는 경우
            order.addProductInOrder(new ProductInOrder(order, requestDto.getProduct_id()));
            List<Long> product_ids = order.getProduct_ids().stream().map(ProductInOrder::getProduct_id).toList();
            return new OrderResponseDto(order.getOrder_id(), product_ids);
        } else{
            throw new IllegalArgumentException(requestDto.getProduct_id() + "번 상품을 찾을 수 없습니다.");
        }
    }

    /*주문이 존재하는지 확인 후 응답 DTO 형태로 반환*/
    @Transactional
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException((orderId + "번 주문을 찾을 수 없습니다.")));
        List<Long> product_ids = order.getProduct_ids().stream().map(ProductInOrder::getProduct_id).toList();
        return new OrderResponseDto(order.getOrder_id(), product_ids);
    }

    /*상품 목록 조회 API 호출, product_ids의 모든 값이 상품 목록에 존재하는지 확인하고 유효 상품은 주문 목록에 추가*/
    public void checkProductList(OrderRequestDto requestDto, Order order) {

        List<ProductInOrder> product_ids = new ArrayList<>();   //요청 받은 상품 목록에서 존재하는 것만 저장
        for(Long product_id : requestDto.getProduct_ids()){
            if(checkProduct(product_id)){             //전체 상품 목록 중 product_id 값이 있는지 확인
                ProductInOrder product = new ProductInOrder(order, product_id);
                product_ids.add(product);                       //유효한 상품 리스트에 저장
            } else{
                throw new IllegalArgumentException(product_id + "번 상품을 찾을 수 없습니다.");
            }
        }
        order.setProduct_ids(product_ids);
    }

    /* 단일 상품 유효성 검사 메서드 */
    private boolean checkProduct(Long product_id){
        List<ProductInfo> productList = productClient.getProducts();
        Map<Long, ProductInfo> productMap = productList.stream().collect(Collectors.toMap(ProductInfo::getProduct_id, Function.identity()));
        return productMap.containsKey(product_id);
    }

    /*fallbackMethod: 주문 생성 실패 시*/
    public OrderResponseDto fallbackCreateOrder(OrderRequestDto requestDto, Throwable throwable) {
        log.error("Fallback: 상품 서비스 호출 또는 주문 생성 중 오류 발생", throwable);
        if(throwable instanceof IllegalArgumentException){
            throw (IllegalArgumentException) throwable;
        } else{
            throw new RuntimeException("상품 서비스를 이용할 수 없습니다. 나중에 다시 시도해주세요.");
        }
    }

    /*fallbackMethod: 기존 주문에 상품 추가 실패 시*/
    public OrderResponseDto fallbackAddProduct(Long orderId, ProductInOrderRequestDto requestDto, Throwable throwable) {
        log.error("Fallback: 기존 주문에 상품 추가 실패 또는 상품 서비스 호출 오류 발생", throwable);
        if(throwable instanceof IllegalArgumentException){
            throw (IllegalArgumentException) throwable;
        } else{
            throw new RuntimeException("상품 서비스를 이용할 수 없습니다. 나중에 다시 시도해주세요.");
        }
    }
}