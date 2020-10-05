package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammeLegalStatusApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatusWrapper
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.ProgrammeLegalStatusService
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammeLegalStatusController(
    private val programmeLegalStatusService: ProgrammeLegalStatusService
) : ProgrammeLegalStatusApi {

    override fun getProgrammeLegalStatusList(pageable: Pageable): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusService.get()
    }

    override fun addProgrammeLegalStatuses(statusData: InputProgrammeLegalStatusWrapper): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusService.save(statusData.statuses)
    }

    override fun delete(id: Long): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusService.delete(legalStatusId = id)
    }

}
