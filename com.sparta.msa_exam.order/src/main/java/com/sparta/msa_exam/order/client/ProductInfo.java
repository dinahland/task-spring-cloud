package com.sparta.msa_exam.order.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductInfo {
    private Long product_id;
    private String name;
    private Integer supply_price;
}
