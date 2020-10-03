package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.SystemLanguage
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.api.programme.dto.SystemLanguageSelection
import io.cloudflight.jems.api.programme.dto.getSystemLanguageSelectionsAsString
import io.cloudflight.jems.server.entity.ProgrammeData
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts

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
    languagesSystem = getSystemLanguageSelectionsAsString(),
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
    systemLanguageSelections = getSystemLanguageSelectionList(),
    programmeNuts = groupNuts(programmeNuts)
)

fun ProgrammeData.getSystemLanguageSelectionList(): List<SystemLanguageSelection> {
    val selectedLanguages = languagesSystem ?: "EN"
    return SystemLanguage.values().map {
        SystemLanguageSelection(it, it.translationKey, it.isSelected(selectedLanguages))
    }
}
