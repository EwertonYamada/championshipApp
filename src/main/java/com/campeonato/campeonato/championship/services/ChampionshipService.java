package com.campeonato.campeonato.championship.services;

import com.campeonato.campeonato.championship.Dto.ChampionshipStartDto;
import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.championship.repository.ChampionshipRepository;
import com.campeonato.campeonato.classificationTable.domain.ClassificationTableDomain;
import com.campeonato.campeonato.classificationTable.repository.ClassificationTableRepository;
import com.campeonato.campeonato.classificationTable.services.ClassificationTableService;
import com.campeonato.campeonato.matches.repository.MatchesRepository;
import com.campeonato.campeonato.teams.domain.TeamDomain;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import com.campeonato.campeonato.teams.services.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChampionshipService {

    final ChampionshipRepository championshipRepository;
    final MatchesRepository matchesRepository;
    final ClassificationTableService classificationTableService;
    final TeamService teamService;
    final ClassificationTableRepository classificationTableRepository;
    final TeamRepository teamRepository;

    public ChampionshipService(ChampionshipRepository championshipRepository, MatchesRepository matchesRepository,
                               ClassificationTableService classificationTableService, TeamService teamService,
                               ClassificationTableRepository classificationTableRepository, TeamRepository teamRepository) {
        this.championshipRepository = championshipRepository;
        this.matchesRepository = matchesRepository;
        this.classificationTableService = classificationTableService;
        this.teamService = teamService;
        this.classificationTableRepository = classificationTableRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public ChampionshipDomain saveChampionship(ChampionshipDomain championshipDomain) {
        validateNewChampionship(championshipDomain);
        return this.championshipRepository.save(championshipDomain);
    }

    @Transactional(readOnly = true)
    public Page<ChampionshipDomain> findAll(Pageable pageable) {
        return this.championshipRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ChampionshipDomain findById(Long id) {
        return this.championshipRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "championship not found");
        });
    }

    @Transactional
    public ChampionshipDomain replaceChampionship(long id, ChampionshipDomain championshipDomain) {
        validateNewChampionship(championshipDomain);
        ChampionshipDomain newChampionshipDomain = this.championshipRepository.findById(id).orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
        );
        newChampionshipDomain.setChampionshipName(championshipDomain.getChampionshipName());
        newChampionshipDomain.setChampionshipYear(championshipDomain.getChampionshipYear());
        return this.championshipRepository.save(newChampionshipDomain);
    }

    @Transactional
    public void startChampionship(ChampionshipStartDto championshipStartDto) {
        validateStartChampionship(championshipStartDto);
        ChampionshipDomain championshipDomain = this.findById(championshipStartDto.getChampionshipId());
        championshipDomain.setStarted(true);
        this.championshipRepository.save(championshipDomain);
        createClassificationForEachTeam(championshipStartDto, championshipDomain);
    }

    @Transactional
    public void finishChampionship(long id) {
        validateFinishChampionship(id);
        ChampionshipDomain championshipDomain = findById(id);
        championshipDomain.setFinished(true);
        championshipDomain.setStarted(false);
        this.championshipRepository.save(championshipDomain);
    }

    @Transactional
    public void deleteChampionship(long id) {
        this.championshipRepository.deleteById(id);
    }

    private void validateNewChampionship(ChampionshipDomain championshipDomain) {
        yearValidator(championshipDomain);
        validateChampionshipExists(championshipDomain);
        validateStatusOnPost(championshipDomain);
    }

    private void validateStartChampionship(ChampionshipStartDto championshipStartDto) {
        validateChampionshipStartedOrFinished(championshipStartDto);
        validateTeamsNumbersOfTeams(championshipStartDto);
        validateRepeatedTeamsInTheChampionship(championshipStartDto);
    }

    private void validateFinishChampionship(long id) {
        validateMatchesNotFinishedInTheChampionship(id);
        validateNumberOfMatches(id);
        validateFinishedOrNotStartedChampionship(id);
    }

    private void yearValidator(ChampionshipDomain championshipDomain) {
        LocalDateTime now = LocalDateTime.now();
        if (championshipDomain.getChampionshipYear() < now.getYear()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ano do campeonato deve ser igual ou superior ao atual");
        }
    }

    private void validateChampionshipExists(ChampionshipDomain championshipDomain) {
        if (this.championshipRepository.existsByNameAndYear(championshipDomain.getChampionshipName(), championshipDomain.getChampionshipYear())) {
            throw new RuntimeException("Campeonato já cadastrado");
        }
    }

    private void validateStatusOnPost(ChampionshipDomain championshipDomain) {
        if (championshipDomain.getStarted() || championshipDomain.getFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status (started/finished) devem ser falsos");
        }
    }

    private void validateMatchesNotFinishedInTheChampionship(long id) {
        if (this.matchesRepository.matchesNotFinishedInTheChampionship(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato possui jogo em aberto ou em andamento");
        }
    }

    private void validateNumberOfMatches(long id) {
        long totalMatches = this.classificationTableRepository.countTeamsInTheChampionship(id) *
                (this.classificationTableRepository.countTeamsInTheChampionship(id) - 1);
        if (this.matchesRepository.countMatchesDomainByChampionship(id) != totalMatches) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os times devem se enfrentar 2 vezes!");
        }
    }

    public void validateFinishedOrNotStartedChampionship(long id) {
        if (championshipRepository.finishedChampionship(id) || (!championshipRepository.startedChampionship(id) &&
                !championshipRepository.finishedChampionship(id))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato já finalizado ou não inicializado");
        }
    }

    public void validateChampionshipStartedOrFinished(ChampionshipStartDto championshipStartDto) {
        if (this.championshipRepository.startedChampionship(championshipStartDto.getChampionshipId()) ||
                this.championshipRepository.finishedChampionship(championshipStartDto.getChampionshipId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato já inicializado ou finalizado");
        }
    }

    public void validateTeamsNumbersOfTeams(ChampionshipStartDto championshipStartDto) {
        if (championshipStartDto.getTeamIds().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para iniciar um campeonato é necessário ao menos 2 equipes");
        }
    }

    public void validateRepeatedTeamsInTheChampionship(ChampionshipStartDto championshipStartDto) {
        Set<Long> set = new HashSet<>();
        championshipStartDto.getTeamIds().forEach(team -> {
            if (!set.add(team)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team id repeated");
            }
        });
    }

    public void createClassificationForEachTeam(ChampionshipStartDto championshipStartDto,
                                                ChampionshipDomain championshipDomain) {
        for (int i = 0; i < championshipStartDto.getTeamIds().size(); i++) {
            ClassificationTableDomain classificationTableDomain = createClassificationTableDomain(championshipDomain,
                    this.teamService.findById(championshipStartDto.getTeamIds().get(i)));
            this.classificationTableRepository.save(classificationTableDomain);
        }
    }

    private ClassificationTableDomain createClassificationTableDomain(ChampionshipDomain championshipDomain,
                                                                      TeamDomain teamDomain) {
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
        return classificationTableDomain;
    }
}