package com.campeonato.campeonato.classificationTable.repository;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.classificationTable.domain.ClassificationTableDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationTableRepository extends JpaRepository<ClassificationTableDomain, Long> {

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM classification_table_domain ctd " +
                    "WHERE ctd.team_id = :teamId " +
                    "   AND ctd.championship_id = :championshipId")
    boolean teamExistsInTheChampionship(@Param("teamId") long teamId, @Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*)  " +
                    "FROM classification_table_domain ctd " +
                    "WHERE ctd.championship_id = :championshipId ")
    long countTeamsByChampionshipId(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT *  " +
                    "FROM classification_table_domain ctd " +
                    "WHERE ctd.championship_id = :championshipId " +
                    "AND  ctd.team_id = :teamId")
    ClassificationTableDomain selectTeam(@Param("championshipId") long championshipId, @Param("teamId") long teamId);


}
