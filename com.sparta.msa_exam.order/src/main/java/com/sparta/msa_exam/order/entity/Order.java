package com.sparta.msa_exam.order.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name="order_db")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="order_product", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "product_id")
    private List<Long> product_ids;
}
