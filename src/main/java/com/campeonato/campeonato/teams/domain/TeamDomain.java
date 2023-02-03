package com.campeonato.campeonato.teams.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TeamDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long teamId;
    private String teamName;

}
