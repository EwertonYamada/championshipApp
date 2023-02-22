package com.campeonato.campeonato.teams.services;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //FUNÇÕES VALIDAÇÃO
    private void validateNewTeam(TeamDomain teamDomain) {
        validateTeamExists(teamDomain);
    }

    private void validateTeamExists(TeamDomain teamDomain) {
        if (teamRepository.existsByTeamName(teamDomain.getTeamName())) {
            throw new RuntimeException("Time já cadastrado");
        }
    }

    // POST
    @Transactional
    public TeamDomain saveTeam(TeamDomain teamDomain) {
        validateNewTeam(teamDomain);
        return teamRepository.save(teamDomain);
    }

    //GET TODOS TIMES
    public Page<TeamDomain> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    //GET POR ID
    public TeamDomain findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time inexistente!");
        });
    }

    // PUT
    @Transactional
    public TeamDomain replace(TeamDomain teamDomain) {
        validateTeamExists(teamDomain);
        return teamRepository.save(teamDomain);
    }

    //DELETE
    @Transactional
    public void deleteTeam(long id) {
        teamRepository.deleteById(id);
    }
}