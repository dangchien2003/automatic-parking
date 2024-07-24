package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findCustomerByEmail(String email);
}
