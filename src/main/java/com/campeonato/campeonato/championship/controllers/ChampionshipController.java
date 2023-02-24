package com.campeonato.campeonato.championship.controllers;

import com.campeonato.campeonato.championship.Dto.ChampionshipStartDto;
import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.championship.services.ChampionshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/campeonatos")
public class ChampionshipController {
    final ChampionshipService championshipService;

    @Autowired
    public ChampionshipController(ChampionshipService championshipService) {

        this.championshipService = championshipService;
    }

    //POST
    @PostMapping
    public ResponseEntity<Object> saveChampionship(@RequestBody @Valid ChampionshipDomain championshipDomain) {
        return ResponseEntity.ok(this.championshipService.saveChampionship(championshipDomain));
    }

    @GetMapping
    public ResponseEntity<Page<ChampionshipDomain>> getAllChampionship(@PageableDefault(page = 0, size = 10,
            sort = "championshipYear", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.championshipService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getByid(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.championshipService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> replaceChampionship(@PathVariable("id") long id,
                                                      @RequestBody @Valid ChampionshipDomain championshipDomain) {
        this.championshipService.replaceChampionship(id, championshipDomain);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Object> startChampionship(@PathVariable("id") long id,
                                                    @RequestBody @Valid ChampionshipStartDto championshipStartDto) {
        this.championshipService.startChampionship(championshipStartDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<Object> finishChampionship(@PathVariable("id") long id) {
        this.championshipService.finishChampionship(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteChampionship(@PathVariable long id) {
        this.championshipService.deleteChampionship(id);
        return ResponseEntity.accepted().build();
    }
}