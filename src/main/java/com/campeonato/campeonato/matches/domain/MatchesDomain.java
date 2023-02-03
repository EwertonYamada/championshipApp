package com.campeonato.campeonato.matches.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class MatchesDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long gameId;
    private String championship; // campeonato ou amistoso?
    private String homeTeam; // time mandante
    private String visitingTeam; // time visitante
    private int visitingTeamGoals; // gols marcados time visitante
    private int homeTeamGoals; // gols marcados time mandante
    private boolean matchWinner; // vencedor da partida
    private boolean matchLoser; // perdedor da partida


}
