package com.campeonato.campeonato.championship.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ChampionshipDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long championshipId;
    private String championshipName;
    private int championshipYear;
    private String startDate;
    private String finishDate;
    private int roundNumber;   //Rodada

}
