package com.campeonato.campeonato.classificationTable.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class ClassificationTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long classificationTableId;

    private String teamName;
    private int totalScore;
    private int wins;   // vitorias
    private int losses; // derrotas
    private int draws; // empates
    private int totalGoalsScored; // total gols marcados
    private int totalGoalsConceded; // total gols sofridos
    private int goalsDifference;    //saldo de gols

}
