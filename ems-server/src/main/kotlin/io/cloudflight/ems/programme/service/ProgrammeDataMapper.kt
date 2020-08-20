package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.api.programme.dto.OutputProgrammeData
import io.cloudflight.ems.entity.ProgrammeData
import io.cloudflight.ems.nuts.entity.NutsRegion3
import io.cloudflight.ems.nuts.service.groupNuts

fun InputProgrammeData.toEntity(programmeNuts: Set<NutsRegion3>) = ProgrammeData(
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
    programmeNuts = programmeNuts
)

fun ProgrammeData.toOutputProgrammeData() = OutputProgrammeData(
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
    programmeNuts = groupNuts(programmeNuts)
)
