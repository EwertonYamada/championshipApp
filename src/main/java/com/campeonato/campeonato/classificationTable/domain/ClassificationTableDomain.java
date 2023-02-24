package com.campeonato.campeonato.classificationTable.domain;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.teams.domain.TeamDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "classifications")
public class ClassificationTableDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_id")
    private long classificationTableId;
    @ManyToOne
    @JoinColumn(name = "championship_id")
    private ChampionshipDomain championship;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamDomain team;
    @Column(name = "total_goals_scored")
    private long totalGoalsScored;
    @Column(name = "total_goals_conceded")
    private long totalGoalsConceded;
    @Column(name = "wins")
    private long wins;
    @Column(name = "losses")
    private long losses;
    @Column(name = "draws")
    private long draws;
    @Column(name = "total_score")
    private long totalScore;
    @Column(name = "goals_difference")
    private long goalsDifference;

}
