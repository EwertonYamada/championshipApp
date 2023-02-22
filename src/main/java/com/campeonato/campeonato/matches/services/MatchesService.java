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
import com.campeonato.campeonato.teams.domain.TeamDomain;
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
        if(matchesDto.getMatchDate().compareTo(currentDate) < 0 )  {
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

    private void validateFinishMatch(long id) {
        validateNotInProgressOrFinished(id);
        MatchesDomain finishMatchDomain = matchesRepository.findById(id).get();
        finishMatchDomain.setFinishedMatch(true);
        finishMatchDomain.setInProgress(false);
        this.matchesRepository.save(finishMatchDomain);

        if (Objects.nonNull(finishMatchDomain.getChampionship())) {
            long difGoals = finishMatchDomain.getHomeTeamGoals() - finishMatchDomain.getVisitingTeamGoals();

            ClassificationTableDomain homeTeamClassification = classificationTableRepository.selectTeam(
                    finishMatchDomain.getChampionship().getChampionshipId(), finishMatchDomain.getHomeTeam().getTeamId());

            ClassificationTableDomain visitingTeamClassification = classificationTableRepository.selectTeam(
                    finishMatchDomain.getChampionship().getChampionshipId(), finishMatchDomain.getVisitingTeam().getTeamId());

            homeTeamClassification.setTotalGoalsScored(homeTeamClassification.getTotalGoalsScored() + finishMatchDomain.getHomeTeamGoals());
            homeTeamClassification.setTotalGoalsConceded(homeTeamClassification.getTotalGoalsConceded() + finishMatchDomain.getVisitingTeamGoals());
            homeTeamClassification.setGoalsDifference(homeTeamClassification.getGoalsDifference() + difGoals);

            visitingTeamClassification.setTotalGoalsScored(visitingTeamClassification.getTotalGoalsScored() + finishMatchDomain.getVisitingTeamGoals());
            visitingTeamClassification.setTotalGoalsConceded(visitingTeamClassification.getTotalGoalsConceded() + finishMatchDomain.getHomeTeamGoals());
            visitingTeamClassification.setGoalsDifference(visitingTeamClassification.getGoalsDifference() - difGoals);

            if (difGoals > 0) {
                setMatchPoints(homeTeamClassification, visitingTeamClassification);
            } else if (difGoals == 0) {
                setDraws(homeTeamClassification);
                setDraws(visitingTeamClassification);
            } else {
                setMatchPoints(visitingTeamClassification, homeTeamClassification);
            }
            classificationTableRepository.save(homeTeamClassification);
            classificationTableRepository.save(visitingTeamClassification);
        }
    }

    //MÉTODOS HTTP
    //POST
    @Transactional
    public MatchesDomain saveMatch(MatchesDto matchesDto) {
        validateNewMatch(matchesDto);
        TeamDomain homeTeam = teamService.findById(matchesDto.getHomeTeamId());
        TeamDomain visitingTeam = teamService.findById(matchesDto.getVisitingTeamId());
        MatchesDomain matchesDomain = new MatchesDomain();

        if (Objects.nonNull(matchesDto.getChampionshipId())) {
            ChampionshipDomain championship = championshipService.findById(matchesDto.getChampionshipId());
            matchesDomain.setChampionship(championship);
        }
        matchesDomain.setHomeTeam(homeTeam);
        matchesDomain.setVisitingTeam(visitingTeam);
        matchesDomain.setHomeTeamGoals(0);
        matchesDomain.setVisitingTeamGoals(0);
        matchesDomain.setFinishedMatch(false);
        matchesDomain.setInProgress(false);
        matchesDomain.setMatchDate(matchesDto.getMatchDate());
        return matchesRepository.save(matchesDomain);
    }

    //GET POR ID
    public MatchesDto findById(Long id) {
        MatchesDomain matchDomain = matchesRepository.findById(id).get();
        MatchesDto matchDto = new MatchesDto(matchDomain);
        return matchDto;
    }

    //GET TODAS PARTIDAS
    public Page<MatchesDomain> findAll(Pageable pageable) {
        return matchesRepository.findAll(pageable);
    }

    // PUT TIMES DA PARTIDA
    @Transactional
    public MatchesDomain replaceMatch(long id, MatchesDto matchesDto) {
        validateInProgressOrFinished(id);
        validateNewMatch(matchesDto);
        MatchesDomain updateMatch = matchesRepository.findById(id).get();
        updateMatch.setHomeTeam(updateMatch.getHomeTeam());
        updateMatch.setVisitingTeam(updateMatch.getVisitingTeam());
        return this.matchesRepository.save(updateMatch);
    }

    //PUT PLACAR DA PARTIDA
    @Transactional
    public MatchesDomain updateMatchInProgress(long id, MatchInProgressDto matchInProgressDto) {
        validateNotInProgressOrFinished(id);
        MatchesDomain matchesDomain = matchesRepository.findById(id).get();
        matchesDomain.setHomeTeamGoals(matchInProgressDto.getHomeTeamGoals());
        matchesDomain.setVisitingTeamGoals(matchInProgressDto.getVisitingTeamGoals());
        return matchesRepository.save(matchesDomain);
    }

    //PUT FINALIZAR JOGO
    @Transactional
    public void finishMatch(long id) {
        validateFinishMatch(id);
    }

    //PUT INICIALIZAR PARTIDA
    @Transactional
    public void startMatch(long id) {
        validateInProgressOrFinished(id);
        MatchesDomain startMatch = matchesRepository.findById(id).get();
        startMatch.setInProgress(true);
        matchesRepository.save(startMatch);
    }

    //DELETE PARTIDA
    @Transactional
    public void deleteMatch(Long id) {
        validateInProgressOrFinished(id);
        matchesRepository.deleteById(id);
    }
}
