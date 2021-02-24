package io.cloudflight.jems.server.programme.controller.legalstatus

import io.cloudflight.jems.api.programme.legalstatus.ProgrammeLegalStatusApi
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusUpdateDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.server.programme.service.legalstatus.get_legal_statuses.GetLegalStatusInteractor
import io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses.UpdateLegalStatusInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeLegalStatusController(
    private val getLegalStatus: GetLegalStatusInteractor,
    private val updateLegalStatus: UpdateLegalStatusInteractor,
) : ProgrammeLegalStatusApi {

    override fun getProgrammeLegalStatusList(): List<ProgrammeLegalStatusDTO> =
        getLegalStatus.getLegalStatuses().toDto()

    override fun updateProgrammeLegalStatuses(statusData: ProgrammeLegalStatusUpdateDTO): List<ProgrammeLegalStatusDTO> =
        updateLegalStatus.updateLegalStatuses(
            toDeleteIds = statusData.toDeleteIds,
            toPersist = statusData.toPersist.toModel(),
        ).toDto()

}
