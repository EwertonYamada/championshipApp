package com.campeonato.campeonato.championship.services;

import com.campeonato.campeonato.championship.Dto.ChampionshipStartDto;
import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.championship.repository.ChampionshipRepository;
import com.campeonato.campeonato.classificationTable.domain.ClassificationTableDomain;
import com.campeonato.campeonato.classificationTable.repository.ClassificationTableRepository;
import com.campeonato.campeonato.classificationTable.services.ClassificationTableService;
import com.campeonato.campeonato.matches.repository.MatchesRepository;
import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.services.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class ChampionshipService {

    final ChampionshipRepository championshipRepository;
    final MatchesRepository matchesRepository;
    final ClassificationTableService classificationTableService;
    final TeamService teamService;
    final ClassificationTableRepository classificationTableRepository;


    public ChampionshipService(ChampionshipRepository championshipRepository, MatchesRepository matchesRepository,
                               ClassificationTableService classificationTableService, TeamService teamService,
                               ClassificationTableRepository classificationTableRepository) {
        this.championshipRepository = championshipRepository;
        this.matchesRepository = matchesRepository;
        this.classificationTableService = classificationTableService;
        this.teamService = teamService;
        this.classificationTableRepository = classificationTableRepository;

    }

    // VALIDADORES
    private void validateNewChampionship(ChampionshipDomain championshipDomain) {

        yearValidator(championshipDomain.getChampionshipYear());
        validateChampionshipExists(championshipDomain);
        if (championshipDomain.getStarted() || championshipDomain.getFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status (started/finished) devem ser falsos");
        }
    }

    private void validateFinishChampionship(long id) {

        if (matchesRepository.matchesNotFinishedInTheChampionship(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato possui jogo em aberto ou em andamento");
        }

        long totalMatches = classificationTableRepository.countTeamsByChampionshipId(id) *
                (classificationTableRepository.countTeamsByChampionshipId(id) - 1);
        if (matchesRepository.countMatchesDomainByChampionship(id) != totalMatches) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os times devem se enfrentar 2 vezes!");
        }
    }

    private void validateChampionshipExists(ChampionshipDomain championshipDomain) {
        if (championshipRepository.existsByNameAndYear(championshipDomain.getChampionshipName(), championshipDomain.getChampionshipYear())) {
            throw new RuntimeException("Campeonato já cadastrado");
        }
    }

    private void yearValidator(int championshipYear) {
        LocalDateTime now = LocalDateTime.now();
        if (championshipYear < now.getYear()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ano do campeonato deve ser igual ou superior ao atual");
        }
    }

    //POST   OK
    @Transactional
    public ChampionshipDomain saveChampionship(ChampionshipDomain championshipDomain) {
        validateNewChampionship(championshipDomain);
        return championshipRepository.save(championshipDomain);
    }

    //GET ALL  OK
    public Page<ChampionshipDomain> findAll(Pageable pageable) {
        return championshipRepository.findAll(pageable);
    }

    //GET POR id

    public ChampionshipDomain findById(Long id) {
        return championshipRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"championship not found");
        });
    }

    //REPLACE   OK
    @Transactional
    public ChampionshipDomain replaceChampionship(long id, ChampionshipDomain championshipDomain) {
        ChampionshipDomain newChampionshipDomain = championshipRepository.findById(id).orElseThrow(() -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        );
        if (!Objects.equals(newChampionshipDomain.getChampionshipName(), championshipDomain.getChampionshipName())
                || !Objects.equals(newChampionshipDomain.getChampionshipYear(),
                championshipDomain.getChampionshipYear())) {
            validateNewChampionship(championshipDomain);
            newChampionshipDomain.setChampionshipName(championshipDomain.getChampionshipName());
            newChampionshipDomain.setChampionshipYear(championshipDomain.getChampionshipYear());
        }
        return championshipRepository.save(newChampionshipDomain);
    }

    //DELETE
    @Transactional
    public void deleteChampionship(long id) {
        championshipRepository.deleteById(id);
    }

    @Transactional
    public void startChampionship(ChampionshipStartDto championshipStartDto) {
        if (championshipRepository.startedChampionship(championshipStartDto.getChampionshipId()) ||
                championshipRepository.finishedChampionship(championshipStartDto.getChampionshipId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato já inicializado ou finalizado");
        }
        if (championshipStartDto.getTeamIds().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para iniciar um campeonato é necessário ao menos 2 equipes");
        }
        Set<Long> set = new HashSet<>();
        championshipStartDto.getTeamIds().forEach(team -> {
            if (!set.add(team)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team id repeated");
            }
        });
        ChampionshipDomain championshipDomain = this.findById(championshipStartDto.getChampionshipId());
        championshipDomain.setStarted(true);
        this.championshipRepository.save(championshipDomain);
        for (int i = 0; i < championshipStartDto.getTeamIds().size(); i++) {
            TeamDomain teamDomain = this.teamService.findById(championshipStartDto.getTeamIds().get(i));
            ClassificationTableDomain classificationTableDomain = new ClassificationTableDomain();
            classificationTableDomain.setChampionship(championshipDomain);
            classificationTableDomain.setTeam(teamDomain);
            classificationTableDomain.setWins(0);
            classificationTableDomain.setLosses(0);
            classificationTableDomain.setDraws(0);
            classificationTableDomain.setTotalScore(0);
            classificationTableDomain.setTotalGoalsScored(0);
            classificationTableDomain.setTotalGoalsConceded(0);
            classificationTableDomain.setGoalsDifference(0);
            this.classificationTableRepository.save(classificationTableDomain);
        }
    }

    public void finishChampionship(long id) {
        ChampionshipDomain championshipDomain = this.findById(id);
        if (championshipDomain.getFinished() || !championshipDomain.getStarted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato já finalizado ou não inicializado");
        }
        validateFinishChampionship(id);
        championshipDomain.setFinished(true);
        championshipDomain.setStarted(false);
        this.championshipRepository.save(championshipDomain);
    }
}
