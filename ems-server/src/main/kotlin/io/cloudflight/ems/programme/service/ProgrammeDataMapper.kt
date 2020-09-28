package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.SystemLanguage
import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.api.programme.dto.OutputProgrammeData
import io.cloudflight.ems.api.programme.dto.SystemLanguageSelection
import io.cloudflight.ems.api.programme.dto.getSystemLanguageSelectionsAsString
import io.cloudflight.ems.entity.ProgrammeData
import io.cloudflight.ems.nuts.entity.NutsRegion3
import io.cloudflight.ems.nuts.service.groupNuts
import java.util.stream.Collectors

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
        SystemLanguageSelection(it.name, it.translationKey, it.isSelected(selectedLanguages))
    }
}
