package com.campeonato.campeonato.teams.repository;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<TeamDomain, UUID> {
    boolean existsByTeamName(String teamName);
}
