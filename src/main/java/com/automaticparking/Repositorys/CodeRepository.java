package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.Code;
import com.automaticparking.database.entity.CodeWithBot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface CodeRepository extends JpaRepository<Code, String> {
    @Query("SELECT q FROM Code q WHERE q.uid = :uid ORDER BY q.buyAt DESC")
    List<Code> findByUidOrderByBuyAtDesc(@Param("uid") String uid, Pageable pageable);

    @Query("SELECT c, b.address FROM CodeWithBot c LEFT JOIN c.bot b WHERE c.uid = :uid AND c.qrid = :qrid")
    Optional<CodeWithBot> findCodeWithBot(@Param("uid") String uid, @Param("qrid") String qrid);

    Optional<Code> findByQridAndUid(String qrid, String uid);

    @Query("SELECT c FROM Code c WHERE c.uid = :uid AND ((c.cancleAt = 0 AND c.expireAt > :now) OR (c.checkinAt != 0))")
    List<Code> findAllCodeUse(@Param("uid") String uid, @Param("now") Long now);


    List<Code> findAllByPlate(String plate);
}
