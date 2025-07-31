package com.sparta.msa_exam.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private Integer supply_price;
}
