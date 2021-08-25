package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.programme.entity.ProgrammeData
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ProgrammeDataFactory(
    val legalStatusRepository: ProgrammeLegalStatusRepository,
    val programmeDataRepository: ProgrammeDataRepository
) {

    val legalStatus = ProgrammeLegalStatusEntity(id = 1)
    val programmeData = ProgrammeData(
        id = 1L,
        cci = "cci",
        title = "title",
        version = "version",
        firstYear = 2010,
        lastYear = 2030,
        eligibleFrom = LocalDate.of(2020, 1, 1),
        eligibleUntil = LocalDate.of(2030, 1, 1),
        commissionDecisionNumber = "",
        commissionDecisionDate = LocalDate.of(2020, 1, 1),
        programmeAmendingDecisionNumber = "",
        programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
        projectIdProgrammeAbbreviation = "NL-DE_",
        projectIdUseCallId = true,
        defaultUserRoleId = 2L
    )

    @Transactional
    fun saveLegalStatus(): ProgrammeLegalStatusEntity {
        return legalStatusRepository.save(legalStatus)
    }

    @Transactional
    fun saveProgrammeData(): ProgrammeData {
        return programmeDataRepository.save(programmeData)
    }
}
