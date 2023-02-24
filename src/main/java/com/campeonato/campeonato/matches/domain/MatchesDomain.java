package com.campeonato.campeonato.matches.domain;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.teams.domain.TeamDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "matches")

public class MatchesDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private long gameId;
    @ManyToOne
    @JoinColumn(name = "championship_id")
    private ChampionshipDomain championship;
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private TeamDomain homeTeam;
    @ManyToOne
    @JoinColumn(name = "visiting_team_id")
    private TeamDomain visitingTeam;
    @Column(name = "visiting_team_goals")
    private long visitingTeamGoals;
    @Column(name = "home_team_goals")
    private long homeTeamGoals;
    @Column(name = "finished")
    private boolean finishedMatch = false;
    @Column(name = "in_progress")
    private boolean inProgress = false;
    @Column(name = "match_date")
    private Date matchDate;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public long getVisitingTeamGoals() {
        return visitingTeamGoals;
    }

    public void setVisitingTeamGoals(long visitingTeamGoals) {
        this.visitingTeamGoals = visitingTeamGoals;
    }

    public long getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public void setHomeTeamGoals(long homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
    }

    public ChampionshipDomain getChampionship() {
        return championship;
    }

    public void setChampionship(ChampionshipDomain championship) {
        this.championship = championship;
    }

    public TeamDomain getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamDomain homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamDomain getVisitingTeam() {
        return visitingTeam;
    }

    public void setVisitingTeam(TeamDomain visitingTeam) {
        this.visitingTeam = visitingTeam;
    }

    public boolean getFinishedMatch() {
        return finishedMatch;
    }

    public void setFinishedMatch(boolean finishedMatch) {
        this.finishedMatch = finishedMatch;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }
}
