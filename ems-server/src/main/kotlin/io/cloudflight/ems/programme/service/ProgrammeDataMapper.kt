package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.ProgrammeBasicData
import io.cloudflight.ems.entity.ProgrammeData

fun ProgrammeBasicData.toEntity() = ProgrammeData(
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

fun ProgrammeData.toProgrammeBasicData() = ProgrammeBasicData(
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
