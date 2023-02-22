package com.campeonato.campeonato.matches.repository;

import com.campeonato.campeonato.matches.domain.MatchesDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository

public interface MatchesRepository extends JpaRepository<MatchesDomain, Long> {
    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches_domain md " +
                    "   WHERE md.championship_id = :championshipId " +
                    "   AND md.home_team_id = :homeTeamId" +
                    "   AND md.visiting_team_id = :visitingTeamId")
    boolean matchExists(@Param("championshipId") long championshipId,
                        @Param("homeTeamId") long homeTeamId,
                        @Param("visitingTeamId") long visitingTeamId);

    @Query(nativeQuery = true,
            value = "SELECT count(*)  " +
                    "FROM matches_domain md " +
                    "   WHERE md.championship_id = :championshipId ")
    long countMatchesDomainByChampionship(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches_domain md " +
                    "   WHERE md.championship_id = :championshipId " +
                    "   AND  (md.in_progress = true " +
                    "   OR md.finished_match = false) ")
    boolean matchesNotFinishedInTheChampionship(@Param("championshipId") long championshipId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches_domain md " +
                    "   WHERE md.game_id = :gameId " +
                    "   AND  md.in_progress = true ")
    boolean matchInProgress(@Param("gameId") long gameId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches_domain md " +
                    "   WHERE md.game_id = :gameId " +
                    "   AND  md.finished_match = true ")
    boolean matchFinished(@Param("gameId") long gameId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) > 0 " +
                    "FROM matches_domain md " +
                    "   WHERE md.match_date = :matchDate " +
                    "   AND  (md.home_team_id = :homeTeamId " +
                    "   OR md.visiting_team_id = :homeTeamId " +
                    "   OR md.home_team_id = :visitingTeamId " +
                    "   OR md.visiting_team_id = :visitingTeamId) ")
    boolean findMatchesDomainByMatchDate(@Param("matchDate") Date matchDate,
                                         @Param("homeTeamId") long homeTeamId,
                                         @Param("visitingTeamId") long visitingTeamId);
}