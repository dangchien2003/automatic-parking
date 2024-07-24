package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface StaffRepository extends JpaRepository<Staff, String> {
    Optional<Staff> findByEmail(String email);

    int countByAdmin(int adminValue);

    List<Staff> findByAdminNot(int adminValue);

    List<Staff> findAllBySidOrEmail(String id, String email);
}
