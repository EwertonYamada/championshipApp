package com.campeonato.campeonato.matches.controllers;

import com.campeonato.campeonato.matches.Dto.MatchInProgressDto;
import com.campeonato.campeonato.matches.Dto.MatchesDto;
import com.campeonato.campeonato.matches.domain.MatchesDomain;
import com.campeonato.campeonato.matches.services.MatchesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/partidas")
public class MatchesController {

    final MatchesService matchesService;

    public MatchesController(MatchesService matchesService) {
        this.matchesService = matchesService;
    }

    //CADATRAR JOGO
    @PostMapping
    public ResponseEntity<Object> saveMatch(@RequestBody @Valid MatchesDto matchesDto) {
        return ResponseEntity.ok(matchesService.saveMatch(matchesDto));
    }

    // GET ALL JOGOS
    @GetMapping
    public ResponseEntity<Page<MatchesDomain>> getAllMatches(@PageableDefault(page = 0, size = 10,
            sort = "championship", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(matchesService.findAll(pageable));
    }

    //GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(matchesService.findById(id));
    }

    //PUT ALTERAR JOGO
    @PutMapping("/{id}")
    public ResponseEntity<Object> replaceMatch(@PathVariable("id") long id,
                                               @RequestBody @Valid MatchesDto matchesDto) {
        matchesService.replaceMatch(id, matchesDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //PUT STARTAR JOGO
    @PutMapping("/{id}/start")
    public ResponseEntity<Object> startMatch(@PathVariable("id") long id) {
        matchesService.startMatch(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // PUT ALTERAR PLACAR
    @PutMapping("/{id}/inProgress")
    public ResponseEntity<Object> updateMatchInProgress(@PathVariable(value = "id") long id,
                                                        @RequestBody MatchInProgressDto matchInProgressDto) {
        matchesService.updateMatchInProgress(id, matchInProgressDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //PUT FINALIZAR JOGO
    @PutMapping("/{id}/finish")
    public ResponseEntity<Object> finishMatch(@PathVariable("id") long id) {
        matchesService.finishMatch(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //DELETE JOGO
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMatch(@PathVariable("id") Long id) {
        matchesService.deleteMatch(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}