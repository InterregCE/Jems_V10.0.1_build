package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus

interface ProgrammeLegalStatusService {
    fun get(): List<OutputProgrammeLegalStatus>

    fun save(legalStatuses: Collection<InputProgrammeLegalStatus>): List<OutputProgrammeLegalStatus>

    fun delete(legalStatusId: Long)
}
