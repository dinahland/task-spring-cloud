package com.sparta.msa_exam.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInOrderRequestDto {
    private Long product_id;
}
