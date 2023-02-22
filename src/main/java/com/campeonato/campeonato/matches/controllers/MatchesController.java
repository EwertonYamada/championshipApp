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

    @PostMapping
    public ResponseEntity<Object> saveMatch(@RequestBody @Valid MatchesDto matchesDto) {
        return ResponseEntity.ok(matchesService.saveMatch(matchesDto));
    }

    @GetMapping
    public ResponseEntity<Page<MatchesDomain>> getAllMatches(@PageableDefault(page = 0, size = 10,
            sort = "championship", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(matchesService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(matchesService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> replaceMatch(@PathVariable("id") long id,
                                               @RequestBody @Valid MatchesDto matchesDto) {

        return ResponseEntity.ok(matchesService.replaceMatch(id, matchesDto));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Object> startMatch(@PathVariable("id") long id) {
        matchesService.startMatch(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/inProgress")
    public ResponseEntity<Object> updateMatchInProgress(@PathVariable(value = "id") long id,
                                                        @RequestBody MatchInProgressDto matchInProgressDto) {
        return ResponseEntity.ok(matchesService.updateMatchInProgress(id, matchInProgressDto));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<Object> finishMatch(@PathVariable("id") long id) {
        matchesService.finishMatch(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable("id") Long id) {
        matchesService.deleteMatch(id);
    }
}
