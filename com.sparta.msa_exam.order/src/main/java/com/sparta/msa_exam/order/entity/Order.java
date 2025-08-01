package com.sparta.msa_exam.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name="order_db")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    @Setter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductInOrder> product_ids = new ArrayList<>();

    public Order(List<ProductInOrder> product_ids) {
        this.product_ids = product_ids;
    }

    public void addProductInOrder(ProductInOrder productInOrder){
        this.product_ids.add(productInOrder);
    }
}
