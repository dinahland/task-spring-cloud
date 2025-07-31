package com.sparta.msa_exam.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private Integer supply_price;
}
