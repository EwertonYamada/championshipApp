package com.campeonato.campeonato.matches.repository;

import com.campeonato.campeonato.matches.domain.MatchesDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository

public interface MatchesRepository extends JpaRepository<MatchesDomain, Long> {
    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches m " +
                    "   WHERE m.championship_id = :championshipId " +
                    "   AND m.home_team_id = :homeTeamId" +
                    "   AND m.visiting_team_id = :visitingTeamId")
    boolean matchExists(@Param("championshipId") long championshipId,
                        @Param("homeTeamId") long homeTeamId,
                        @Param("visitingTeamId") long visitingTeamId);

    @Query(nativeQuery = true,
            value = "SELECT count(*)  " +
                    "FROM matches m " +
                    "   WHERE m.championship_id = :championshipId ")
    long countMatchesDomainByChampionship(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches m " +
                    "   WHERE m.championship_id = :championshipId " +
                    "   AND  (m.in_progress = true " +
                    "   OR m.finished = false) ")
    boolean matchesNotFinishedInTheChampionship(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches m " +
                    "   WHERE m.game_id = :gameId " +
                    "   AND  m.in_progress = true ")
    boolean matchInProgress(@Param("gameId") long gameId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches m " +
                    "   WHERE m.game_id = :gameId " +
                    "   AND  m.finished = true ")
    boolean matchFinished(@Param("gameId") long gameId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches m " +
                    "   WHERE m.match_date = :matchDate " +
                    "   AND  (m.home_team_id = :homeTeamId " +
                    "   OR m.visiting_team_id = :homeTeamId " +
                    "   OR m.home_team_id = :visitingTeamId " +
                    "   OR m.visiting_team_id = :visitingTeamId) ")
    boolean findMatchesDomainByMatchDate(@Param("matchDate") Date matchDate,
                                         @Param("homeTeamId") long homeTeamId,
                                         @Param("visitingTeamId") long visitingTeamId);
}