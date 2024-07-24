package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.Cash;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface CashRepository extends JpaRepository<Cash, Long> {
    @Query("SELECT c FROM Cash c WHERE c.uid = :uid ORDER BY c.cashAt DESC")
    List<Cash> findAllByUid(@Param("uid") String uid);

    @Query("SELECT h FROM Cash h WHERE h.uid = :uid AND h.acceptAt IS NOT NULL AND h.recashAt IS NULL AND h.cancleAt IS NULL")
    List<Cash> findHistoryCashOkByUid(@Param("uid") String uid);

    @Query("SELECT c FROM Cash c WHERE c.acceptAt IS NULL AND c.cancleAt IS NULL")
    List<Cash> findAllCashNotApprove();

    @Modifying
    @Transactional
    @Query("UPDATE Cash c SET c.acceptAt = :acceptAt, c.acceptBy = :acceptBy WHERE c.stt IN :listId AND c.acceptAt IS NULL AND c.cancleAt IS NULL")
    int approveListCash(@Param("acceptAt") Long approveAt, @Param("acceptBy") String personApprove, @Param("listId") List<Long> listId);
}
