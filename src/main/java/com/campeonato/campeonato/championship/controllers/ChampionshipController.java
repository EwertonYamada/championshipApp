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
        return ResponseEntity.ok(championshipService.saveChampionship(championshipDomain));
    }

    //GET TODOS CAMPEONATOS
    @GetMapping
    public ResponseEntity<Page<ChampionshipDomain>> getAllChampionship(@PageableDefault(page = 0, size = 10,
            sort = "championshipYear", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(championshipService.findAll(pageable));
    }

    //GET CAMPEONATO POR NOME e ANO
    @GetMapping("/{id}")
    public ResponseEntity<Object> getByid(@PathVariable("id") long id) {
        return ResponseEntity.ok(championshipService.findById(id));
    }

    //PUT PARA ATUALIZAR O CAMPEONATO
    @PutMapping("/{id}")
    public ResponseEntity<Object> replaceChampionship(@PathVariable("id") long id,
                                                      @RequestBody @Valid ChampionshipDomain championshipDomain) {
        return ResponseEntity.ok(championshipService.replaceChampionship(id, championshipDomain));
    }

    //PUT PARA STARTAR O CAMPEONATO
    @PutMapping("/{id}/start")
    public ResponseEntity<Object> startChampionship(@PathVariable("id") long id,
                                                    @RequestBody @Valid ChampionshipStartDto championshipStartDto) {
        championshipService.startChampionship(championshipStartDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<Object> finishChampionship(@PathVariable("id") long id) {
        championshipService.finishChampionship(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteChampionship(@PathVariable long id) {
        championshipService.deleteChampionship(id);
        return ResponseEntity.accepted().build();
    }
}