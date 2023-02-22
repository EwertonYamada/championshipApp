package com.campeonato.campeonato.championship.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ChampionshipStartDto {
    private long championshipId;
    @NotEmpty
    private List<Long> teamIds;
}
