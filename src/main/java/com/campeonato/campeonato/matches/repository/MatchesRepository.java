package com.campeonato.campeonato.matches.repository;

import com.campeonato.campeonato.matches.domain.MatchesDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface MatchesRepository extends JpaRepository<MatchesDomain, UUID> {
}
