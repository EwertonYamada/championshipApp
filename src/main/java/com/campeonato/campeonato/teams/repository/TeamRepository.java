package com.campeonato.campeonato.teams.repository;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<TeamDomain, Long> {
    boolean existsByTeamName(String teamName);

    TeamDomain existsByTeamId(Long id);


}
