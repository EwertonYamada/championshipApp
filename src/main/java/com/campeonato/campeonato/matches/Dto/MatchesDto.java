package com.campeonato.campeonato.matches.Dto;

import com.campeonato.campeonato.matches.domain.MatchesDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MatchesDto {
    private Long championshipId;
    @NotNull
    private long homeTeamId;
    @NotNull
    private long visitingTeamId;
    @Min(value = 0)
    private long visitingTeamGoals;
    @Min(value = 0)
    private long homeTeamGoals;
    private boolean finishedMatch = false;
    private boolean inProgress = false;
    @NotNull
    private Date matchDate;

    public MatchesDto(MatchesDomain matchesDomain) {
        championshipId = matchesDomain.getGameId();
        homeTeamId = matchesDomain.getHomeTeam().getTeamId();
        visitingTeamId = matchesDomain.getVisitingTeam().getTeamId();
        visitingTeamGoals = matchesDomain.getVisitingTeamGoals();
        homeTeamGoals = matchesDomain.getHomeTeamGoals();
        finishedMatch = matchesDomain.getFinishedMatch();
        inProgress = matchesDomain.getInProgress();
        matchDate = matchesDomain.getMatchDate();
    }
}
