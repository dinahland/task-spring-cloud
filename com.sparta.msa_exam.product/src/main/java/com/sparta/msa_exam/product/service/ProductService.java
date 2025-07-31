package com.sparta.msa_exam.product.service;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /*요청 받은 상품 이름, 가격으로 Product 생성, DB 저장 후 응답 반환*/
    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto requestDto) {
        Product newProduct = new Product(requestDto.getName(), requestDto.getSupply_price());
        Product savedProduct = productRepository.save(newProduct);
        return new ProductResponseDto(savedProduct.getProduct_id(), savedProduct.getName(), savedProduct.getSupply_price());
    }

}
