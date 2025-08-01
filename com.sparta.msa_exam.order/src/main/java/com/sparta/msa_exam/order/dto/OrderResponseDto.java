package com.sparta.msa_exam.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long order_id;
    private List<Long> product_ids;
}
