package com.campeonato.campeonato.championship.repository;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChampionshipRepository extends JpaRepository<ChampionshipDomain, Long> {
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM championship_domain cd " +
                    "WHERE cd.championship_name = :name " +
                    "   AND cd.championship_year = :year ")
    Boolean existsByNameAndYear(@Param("name") String name, @Param("year") int year);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM championship_domain cd " +
                    "WHERE cd.championship_id = :championshipId " +
                    "   AND cd.started = true")
    Boolean startedChampionship(@Param("championshipId") long id);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM championship_domain cd " +
                    "WHERE cd.championship_id = :championshipId " +
                    "   AND cd.finished = true")
    Boolean finishedChampionship(@Param("championshipId") long id);
}