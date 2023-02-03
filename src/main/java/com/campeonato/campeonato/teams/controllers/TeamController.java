package com.campeonato.campeonato.teams.controllers;

import com.campeonato.campeonato.teams.services.TeamService;
import com.campeonato.campeonato.teams.teamDto.TeamDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/times")
public class TeamController {

    final
    TeamService teamService;
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Object> saveTeam(TeamDto teamDto) {
        return teamService.existsByTeamName(teamDto);
    }
}
