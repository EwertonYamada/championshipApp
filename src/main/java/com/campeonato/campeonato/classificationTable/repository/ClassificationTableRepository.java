package com.campeonato.campeonato.classificationTable.repository;

import com.campeonato.campeonato.classificationTable.domain.ClassificationTableDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationTableRepository extends JpaRepository<ClassificationTableDomain, Long> {

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM classifications c " +
                    "WHERE c.team_id = :teamId " +
                    "   AND c.championship_id = :championshipId")
    boolean teamExistsInTheChampionship(@Param("teamId") long teamId, @Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*)  " +
                    "FROM classifications c " +
                    "WHERE c.championship_id = :championshipId ")
    long countTeamsInTheChampionship(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT *  " +
                    "FROM classifications c " +
                    "WHERE c.championship_id = :championshipId " +
                    "AND  c.team_id = :teamId")
    ClassificationTableDomain selectTeam(@Param("championshipId") long championshipId, @Param("teamId") long teamId);


}
