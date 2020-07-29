package io.cloudflight.ems.programme

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.entity.ProgrammeData

fun InputProgrammeData.toEntity() = ProgrammeData(
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
    programmeAmendingDecisionDate = programmeAmendingDecisionDate
)

fun ProgrammeData.toInputProgrammeData() = InputProgrammeData(
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
    programmeAmendingDecisionDate = programmeAmendingDecisionDate
)
