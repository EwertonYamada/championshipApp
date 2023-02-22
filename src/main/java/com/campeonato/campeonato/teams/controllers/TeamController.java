package com.campeonato.campeonato.teams.controllers;

import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.services.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/times")
public class TeamController {

    final
    TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    //SALVAR/CADASTRAR TIME
    @PostMapping
    public ResponseEntity<Object> saveTeam(@RequestBody @Valid TeamDomain teamDomain) {
        return ResponseEntity.ok(teamService.saveTeam(teamDomain));
    }

    //GET LISTA TODOS TIMES
    @GetMapping
    public ResponseEntity<Page<TeamDomain>> getAllChampionship(@PageableDefault(page = 0, size = 10,
            sort = "teamName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(teamService.findAll(pageable));
    }

    //GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    //ATUALIZAR/SUBSTITUIR TIME
    @PutMapping
    public ResponseEntity<Object> replace(@RequestBody TeamDomain teamDomain) {
        return ResponseEntity.ok(teamService.replace(teamDomain));
    }

    //DELETAR TIME
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTeam(@PathVariable long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.accepted().build();
    }
}