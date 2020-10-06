package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus

interface ProgrammeLegalStatusService {
    fun get(): List<OutputProgrammeLegalStatus>

    fun save(
        toPersist: Collection<InputProgrammeLegalStatus>,
        toDelete: Collection<InputProgrammeLegalStatus>
    ): List<OutputProgrammeLegalStatus>
}
