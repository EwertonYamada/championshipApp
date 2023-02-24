package com.campeonato.campeonato.matches.services;

import com.campeonato.campeonato.championship.domain.ChampionshipDomain;
import com.campeonato.campeonato.championship.repository.ChampionshipRepository;
import com.campeonato.campeonato.championship.services.ChampionshipService;
import com.campeonato.campeonato.classificationTable.domain.ClassificationTableDomain;
import com.campeonato.campeonato.classificationTable.repository.ClassificationTableRepository;
import com.campeonato.campeonato.matches.Dto.MatchInProgressDto;
import com.campeonato.campeonato.matches.Dto.MatchesDto;
import com.campeonato.campeonato.matches.domain.MatchesDomain;
import com.campeonato.campeonato.matches.repository.MatchesRepository;
import com.campeonato.campeonato.teams.repository.TeamRepository;
import com.campeonato.campeonato.teams.services.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Objects;


@Service
public class MatchesService {

    final MatchesRepository matchesRepository;
    final TeamService teamService;
    final ChampionshipService championshipService;
    final ChampionshipRepository championshipRepository;
    final TeamRepository teamRepository;
    final ClassificationTableRepository classificationTableRepository;

    public MatchesService(MatchesRepository matchesRepository, TeamService teamService,
                          ChampionshipService championshipService, ChampionshipRepository championshipRepository,
                          TeamRepository teamRepository, ClassificationTableRepository classificationTableRepository) {
        this.matchesRepository = matchesRepository;
        this.teamService = teamService;
        this.teamRepository = teamRepository;
        this.championshipService = championshipService;
        this.championshipRepository = championshipRepository;
        this.classificationTableRepository = classificationTableRepository;
    }

    @Transactional
    public MatchesDomain saveMatch(MatchesDto matchesDto) {
        this.validateNewMatch(matchesDto);
        MatchesDomain newMatch = new MatchesDomain();

        if (Objects.nonNull(matchesDto.getChampionshipId())) {
            ChampionshipDomain championship = this.championshipService.findById(matchesDto.getChampionshipId());
            newMatch.setChampionship(championship);
        }

        newMatch.setHomeTeam(this.teamService.findById(matchesDto.getHomeTeamId()));
        newMatch.setVisitingTeam(this.teamService.findById(matchesDto.getVisitingTeamId()));
        newMatch.setHomeTeamGoals(0);
        newMatch.setVisitingTeamGoals(0);
        newMatch.setFinishedMatch(false);
        newMatch.setInProgress(false);
        newMatch.setMatchDate(matchesDto.getMatchDate());
        return this.matchesRepository.save(newMatch);
    }

    @Transactional(readOnly = true)
    public MatchesDto findById(Long id) {
        MatchesDomain matchDomain = this.matchesRepository.findById(id).get();
        MatchesDto matchDto = new MatchesDto(matchDomain);
        return matchDto;
    }

    @Transactional(readOnly = true)
    public Page<MatchesDomain> findAll(Pageable pageable) {
        return this.matchesRepository.findAll(pageable);
    }

    @Transactional
    public MatchesDomain replaceMatch(long id, MatchesDto matchesDto) {
        this.validateInProgressOrFinished(id);
        this.validateNewMatch(matchesDto);
        MatchesDomain updateMatch = this.matchesRepository.findById(id).get();
        updateMatch.setHomeTeam(updateMatch.getHomeTeam());
        updateMatch.setVisitingTeam(updateMatch.getVisitingTeam());
        return this.matchesRepository.save(updateMatch);
    }

    @Transactional
    public MatchesDomain updateMatchInProgress(long id, MatchInProgressDto matchInProgressDto) {
        this.validateNotInProgressOrFinished(id);
        MatchesDomain matchesDomain = this.matchesRepository.findById(id).get();
        matchesDomain.setHomeTeamGoals(matchInProgressDto.getHomeTeamGoals());
        matchesDomain.setVisitingTeamGoals(matchInProgressDto.getVisitingTeamGoals());
        return this.matchesRepository.save(matchesDomain);
    }

    @Transactional
    public void finishMatch(long id) {
        this.validateNotInProgressOrFinished(id);
        MatchesDomain finishMatchDomain = this.matchesRepository.findById(id).get();
        finishMatchDomain.setFinishedMatch(true);
        finishMatchDomain.setInProgress(false);
        this.matchesRepository.save(finishMatchDomain);

        if (Objects.nonNull(finishMatchDomain.getChampionship())) {
            validateFinishMatchOfChampionship(id);
        }
    }

    @Transactional
    public void startMatch(long id) {
        this.validateInProgressOrFinished(id);
        MatchesDomain startMatch = this.matchesRepository.findById(id).get();
        startMatch.setInProgress(true);
        this.matchesRepository.save(startMatch);
    }

    //DELETE PARTIDA
    @Transactional
    public void deleteMatch(Long id) {
        this.validateInProgressOrFinished(id);
        this.matchesRepository.deleteById(id);
    }

    //VALIDADORES
    // VALIDADOR PARA CRIAR UM NOVO JOGO
    private void validateNewMatch(MatchesDto matchesDto) {

        if (Objects.nonNull(matchesDto.getChampionshipId())) {
            validateChampionshipStatus(matchesDto);
            teamExistsInTheChampionship(matchesDto);
            matchExists(matchesDto);
        }
        validateDate(matchesDto);
        validateOpponents(matchesDto);
        validateTeamAvailability(matchesDto);
    }

    //VALIDADOR PARA QUE UM TIME NÃO JOGUE CONTRA ELE MESMO
    private void validateOpponents(MatchesDto matchesDto) {
        if (Objects.equals(matchesDto.getHomeTeamId(), matchesDto.getVisitingTeamId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adversários devem ser diferentes");
        }
    }

    //VALIDADOR PARA QUE NÃO TENHA JOGO EM CAMPEONATO NÃO INICIADO OU JÁ FINALIZADO
    private void validateChampionshipStatus(MatchesDto matchesDto) {
        if (!championshipRepository.startedChampionship(matchesDto.getChampionshipId()) || championshipRepository.finishedChampionship(matchesDto.getHomeTeamId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campeonato não inicializado ou já finalizado");
        }
    }

    private void teamExistsInTheChampionship(MatchesDto matchesDto) {
        if (!classificationTableRepository.teamExistsInTheChampionship(
                matchesDto.getHomeTeamId(), matchesDto.getChampionshipId()) ||
                !classificationTableRepository.teamExistsInTheChampionship(
                        matchesDto.getVisitingTeamId(), matchesDto.getChampionshipId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time não cadastrado no campeonato");
        }
    }

    private void matchExists(MatchesDto matchesDto) {
        if (matchesRepository.matchExists(matchesDto.getChampionshipId(),
                matchesDto.getHomeTeamId(), matchesDto.getVisitingTeamId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogo já realizado com time 1 como mandante");
        }
    }

    private void validateTeamAvailability(MatchesDto matchesDto) {
        if (matchesRepository.findMatchesDomainByMatchDate(matchesDto.getMatchDate(), matchesDto.getHomeTeamId(), matchesDto.getVisitingTeamId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um dos times já possui agenda para este dia");
        }
    }

    public void validateDate(MatchesDto matchesDto) {
        Date currentDate = new Date();
        if (matchesDto.getMatchDate().compareTo(currentDate) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data do jogo deve ser igual ou posterior a atual");
        }
    }

    private void validateInProgressOrFinished(long id) {
        if (matchesRepository.matchFinished(id) || matchesRepository.matchInProgress(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogo em andamento ou já finalizado");
        }
    }

    private void validateNotInProgressOrFinished(long id) {
        if (matchesRepository.matchFinished(id) || !matchesRepository.matchInProgress(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogo não inicializado ou já finalizado");
        }
    }

    private static void setMatchPoints(ClassificationTableDomain winnerTeam, ClassificationTableDomain loserTeam) {
        winnerTeam.setWins(winnerTeam.getWins() + 1);
        winnerTeam.setTotalScore(winnerTeam.getTotalScore() + 3);
        loserTeam.setLosses(loserTeam.getLosses() + 1);
    }

    private static void setDraws(ClassificationTableDomain team) {
        team.setDraws(team.getDraws() + 1);
        team.setTotalScore(team.getTotalScore() + 1);
    }

    private void validateFinishMatchOfChampionship(long id) {
        MatchesDomain finishMatch = this.matchesRepository.findById(id).get();
        long difGoals = finishMatch.getHomeTeamGoals() - finishMatch.getVisitingTeamGoals();

        ClassificationTableDomain homeTeamClassification = classificationTableRepository.selectTeam(
                finishMatch.getChampionship().getChampionshipId(), finishMatch.getHomeTeam().getTeamId());

        ClassificationTableDomain visitingTeamClassification = classificationTableRepository.selectTeam(
                finishMatch.getChampionship().getChampionshipId(), finishMatch.getVisitingTeam().getTeamId());

        homeTeamClassification.setTotalGoalsScored(homeTeamClassification.getTotalGoalsScored() + finishMatch.getHomeTeamGoals());
        homeTeamClassification.setTotalGoalsConceded(homeTeamClassification.getTotalGoalsConceded() + finishMatch.getVisitingTeamGoals());
        homeTeamClassification.setGoalsDifference(homeTeamClassification.getGoalsDifference() + difGoals);

        visitingTeamClassification.setTotalGoalsScored(visitingTeamClassification.getTotalGoalsScored() + finishMatch.getVisitingTeamGoals());
        visitingTeamClassification.setTotalGoalsConceded(visitingTeamClassification.getTotalGoalsConceded() + finishMatch.getHomeTeamGoals());
        visitingTeamClassification.setGoalsDifference(visitingTeamClassification.getGoalsDifference() - difGoals);

        if (difGoals > 0) {
            setMatchPoints(homeTeamClassification, visitingTeamClassification);
        } else if (difGoals == 0) {
            setDraws(homeTeamClassification);
            setDraws(visitingTeamClassification);
        } else {
            setMatchPoints(visitingTeamClassification, homeTeamClassification);
        }
    }
}
