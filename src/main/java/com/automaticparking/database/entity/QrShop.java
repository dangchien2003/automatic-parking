package com.automaticparking.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
