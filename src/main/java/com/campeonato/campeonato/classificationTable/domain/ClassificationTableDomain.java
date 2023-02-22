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
public class ClassificationTableDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long classificationTableId;
    @ManyToOne
    @JoinColumn(name = "championship_id")
    private ChampionshipDomain championship;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamDomain team;
    private long totalGoalsScored; // total gols marcados
    private long totalGoalsConceded; // total gols sofridos
    private long wins;   // vitorias
    private long losses; // derrotas
    private long draws; // empates
    private long totalScore;
    private long goalsDifference;    //saldo de gols

}
