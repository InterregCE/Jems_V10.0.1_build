package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts
import io.cloudflight.jems.server.nuts.service.toOutputNuts
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity

fun ProgrammeDataUpdateRequestDTO.toEntity(
    programmeNuts: Set<NutsRegion3>,
    defaultUserRoleId: Long?
) = ProgrammeDataEntity(
    id = 1,
    cci = cci,
    title = title,
    version = version,
    firstYear = firstYear,
    lastYear = lastYear,
    eligibleFrom = eligibleFrom,
    eligibleUntil = eligibleUntil,
    commissionDecisionNumber = commissionDecisionNumber,
    commissionDecisionDate = commissionDecisionDate,
    programmeAmendingDecisionNumber = programmeAmendingDecisionNumber,
    programmeAmendingDecisionDate = programmeAmendingDecisionDate,
    projectIdProgrammeAbbreviation = projectIdProgrammeAbbreviation,
    projectIdUseCallId = projectIdUseCallId,
    programmeNuts = programmeNuts,
    defaultUserRoleId = defaultUserRoleId
)

fun ProgrammeDataEntity.toProgrammeDataDTO() = ProgrammeDataDTO(
    cci = cci,
    title = title,
    version = version,
    firstYear = firstYear,
    lastYear = lastYear,
    eligibleFrom = eligibleFrom,
    eligibleUntil = eligibleUntil,
    commissionDecisionNumber = commissionDecisionNumber,
    commissionDecisionDate = commissionDecisionDate,
    programmeAmendingDecisionNumber = programmeAmendingDecisionNumber,
    programmeAmendingDecisionDate = programmeAmendingDecisionDate,
    projectIdProgrammeAbbreviation = projectIdProgrammeAbbreviation,
    projectIdUseCallId = projectIdUseCallId,
    programmeNuts = programmeNuts.toDto()
)

fun Set<NutsRegion3>.toDto() = groupNuts(this).toOutputNuts()
