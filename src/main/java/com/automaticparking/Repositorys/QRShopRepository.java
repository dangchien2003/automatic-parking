package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.QrShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface QRShopRepository extends JpaRepository<QrShop, String> {
    List<QrShop> findAllByHide(int hide);
}
