package io.cloudflight.ems.programme.controller

import io.cloudflight.ems.api.programme.ProgrammeLegalStatusApi
import io.cloudflight.ems.api.programme.dto.*
import io.cloudflight.ems.programme.service.ProgrammeLegalStatusService
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammeLegalStatusController (
    private val programmeLegalStatusService: ProgrammeLegalStatusService
) : ProgrammeLegalStatusApi {

    override fun getProgrammeLegalStatusList(pageable: Pageable): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusService.get()
    }

    override fun addProgrammeLegalStatuses(statusData: InputProgrammeLegalStatusWrapper): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusService.save(statusData.statuses)
    }

    override fun delete(id: Long) {
        programmeLegalStatusService.delete(legalStatusId = id)
    }

}
