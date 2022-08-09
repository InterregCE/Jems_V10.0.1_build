package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.ProgrammeDataApi
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import io.cloudflight.jems.server.programme.service.info.hasProjectsInStatus.HasProjectsInStatusInteractor
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeDataController(
    private val programmeDataService: ProgrammeDataService,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val hasProjectsInStatus: HasProjectsInStatusInteractor
) : ProgrammeDataApi {

    override fun get(): ProgrammeDataDTO {
        return programmeDataService.get()
    }

    override fun update(updateRequestDTO: ProgrammeDataUpdateRequestDTO): ProgrammeDataDTO {
        return programmeDataService.update(updateRequestDTO)
    }

    override fun updateNuts(regions: Collection<String>): ProgrammeDataDTO {
        return programmeDataService.saveProgrammeNuts(regions)
    }

    override fun getNuts(): List<OutputNuts> = programmeDataService.getAvailableNuts()

    override fun isProgrammeSetupLocked(): Boolean = isProgrammeSetupLocked.isLocked()

    override fun hasProjectsInStatus(projectStatus: ApplicationStatusDTO): Boolean =
        hasProjectsInStatus.programmeHasProjectsInStatus(projectStatus)

    override fun isAnyReportCreated(): Boolean =
        isProgrammeSetupLocked.isAnyReportCreated()

    override fun isFastTrackLumpSumReadyForPayment(programmeLumpSumId: Long): Boolean =
        isProgrammeSetupLocked.isFastTrackLumpSumReadyForPayment(programmeLumpSumId)
}
