package io.cloudflight.jems.api.programme.dto.stateaid

class ProgrammeStateAidUpdateDTO(
    val toDeleteIds: Set<Long> = emptySet(),
    val toPersist: Collection<ProgrammeStateAidDTO>,
)
