package com.campeonato.campeonato.teams.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class TeamDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long teamId;
    private String teamName;
}
