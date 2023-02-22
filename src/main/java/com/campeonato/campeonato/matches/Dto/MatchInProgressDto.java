package com.campeonato.campeonato.matches.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
public class MatchInProgressDto {
    @Min(value = 0)
    private long visitingTeamGoals;
    @Min(value = 0)
    private long homeTeamGoals;
}
