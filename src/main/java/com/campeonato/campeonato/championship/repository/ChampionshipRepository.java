package com.campeonato.campeonato.championship.repository;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChampionshipRepository extends JpaRepository<ChampionshipDomain, UUID> {
}
