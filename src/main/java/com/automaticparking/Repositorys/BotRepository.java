package com.automaticparking.Repositorys;

import com.automaticparking.database.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface BotRepository extends JpaRepository<Bot, String> {

}
