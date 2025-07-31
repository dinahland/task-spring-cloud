package com.sparta.msa_exam.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long product_id;

    private String name;

    private Integer supply_price;
}
