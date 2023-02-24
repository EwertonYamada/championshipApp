package com.campeonato.campeonato.championship.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Getter
@Setter
@Table(name = "championships")

public class ChampionshipDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "championship_id")
    private long championshipId;
    @NotBlank
    @Column(name = "championship_name")
    private String championshipName;
    @NotNull
    @Column(name = "championship_year")
    private int championshipYear;
    @Column(name = "started")
    private boolean started = false;
    @Column(name = "finished")
    private boolean finished = false;


    public long getChampionshipId() {
        return championshipId;
    }

    public void setChampionshipId(long championshipId) {
        this.championshipId = championshipId;
    }

    public String getChampionshipName() {
        return championshipName;
    }

    public void setChampionshipName(String championshipName) {
        this.championshipName = championshipName;
    }

    public int getChampionshipYear() {
        return championshipYear;
    }

    public void setChampionshipYear(int championshipYear) {
        this.championshipYear = championshipYear;
    }

    public boolean getStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}


