package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.ems.api.programme.dto.OutputProgrammeLegalStatus

interface ProgrammeLegalStatusService {
    fun get(): List<OutputProgrammeLegalStatus>

    fun save(legalStatuses: Collection<InputProgrammeLegalStatus>): List<OutputProgrammeLegalStatus>

    fun delete(legalStatusId: Long)
}
