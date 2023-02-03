package com.campeonato.campeonato.teams.teamDto;

import javax.validation.constraints.NotBlank;

public class TeamDto {
    @NotBlank
    private String teamName;


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
