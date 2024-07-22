package com.automaticparking.database.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopqr")
public class QrShop {
    @Id
    private String qrCategory;

    @Column
    private Integer price;

    @Column
    private Long maxAge;

    @Column
    private Integer hide;
}
