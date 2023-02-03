package com.campeonato.campeonato.teams.services;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import com.campeonato.campeonato.teams.teamDto.TeamDto;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    final
    TeamRepository teamRepository;
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;

    }
    public ResponseEntity<Object> existsByTeamName(TeamDto teamDto) {

        if (teamRepository.existsByTeamName(teamDto.getTeamName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Time já cadastrado");
        }
        TeamDomain teamDomain = new TeamDomain();
        BeanUtils.copyProperties(teamDto, teamDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamRepository.save(teamDomain));

    }
}





