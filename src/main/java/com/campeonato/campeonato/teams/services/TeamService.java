package com.campeonato.campeonato.teams.services;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeamService {

    final
    TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public TeamDomain saveTeam(TeamDomain teamDomain) {
        this.validateNewTeam(teamDomain);
        return teamRepository.save(teamDomain);
    }

    public TeamDomain findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time inexistente!");
        });
    }

    @Transactional
    public TeamDomain replace(TeamDomain teamDomain) {
        this.validateTeamExists(teamDomain);
        return teamRepository.save(teamDomain);
    }

    @Transactional
    public void deleteTeam(long id) {
        teamRepository.deleteById(id);
    }

    private void validateNewTeam(TeamDomain teamDomain) {
        validateTeamExists(teamDomain);
    }

    private void validateTeamExists(TeamDomain teamDomain) {
        if (teamRepository.existsByTeamName(teamDomain.getTeamName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time já cadastrado");
        }
    }
}