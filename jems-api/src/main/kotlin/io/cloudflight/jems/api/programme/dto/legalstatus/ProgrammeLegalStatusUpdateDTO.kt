package io.cloudflight.jems.api.programme.dto.legalstatus

data class ProgrammeLegalStatusUpdateDTO(
    val toDeleteIds: Set<Long> = emptySet(),
    val toPersist: Collection<ProgrammeLegalStatusDTO>,
)
