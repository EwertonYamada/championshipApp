package com.campeonato.campeonato.teams.controllers;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import com.campeonato.campeonato.teams.services.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/times")
public class TeamController {

    final TeamService teamService;
    final TeamRepository teamRepository;

    public TeamController(TeamService teamService, TeamRepository teamRepository) {
        this.teamService = teamService;
        this.teamRepository = teamRepository;
    }

    //SALVAR/CADASTRAR TIME
    @PostMapping
    public ResponseEntity<Object> saveTeam(@RequestBody @Valid TeamDomain teamDomain) {
        return ResponseEntity.ok( this.teamService.saveTeam(teamDomain));
    }

    //GET LISTA TODOS TIMES
    @GetMapping("/all")
    public ResponseEntity<Page<TeamDomain>> getAllChampionship(@PageableDefault(page = 0, size = 10,
            sort = "teamName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(teamRepository.findAll(pageable));
    }

    //GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    //ATUALIZAR/SUBSTITUIR TIME
    @PutMapping
    public ResponseEntity<Object> replace(@RequestBody TeamDomain teamDomain) {
        return ResponseEntity.ok(this.teamService.replace(teamDomain));
    }

    //DELETAR TIME
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTeam(@PathVariable long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.accepted().build();
    }
}