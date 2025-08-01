package com.sparta.msa_exam.order.controller;

import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.ProductInOrderRequestDto;
import com.sparta.msa_exam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /*주문 생성 API*/
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto){
        return ResponseEntity.ok(orderService.createOrder(requestDto));
    }

    /*기존 주문에 상품 추가 API*/
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> addProduct(@PathVariable("orderId") Long orderId, @RequestBody ProductInOrderRequestDto requestDto){
        return ResponseEntity.ok(orderService.addProduct(orderId, requestDto));
    }

    /*주문 단건 조회 API*/
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}

