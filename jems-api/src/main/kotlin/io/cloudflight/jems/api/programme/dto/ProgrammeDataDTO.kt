package io.cloudflight.jems.api.programme.dto

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import java.time.LocalDate

data class ProgrammeDataDTO(

    val cci: String?,
    val title: String?,
    val version: String?,
    val firstYear: Int?,
    val lastYear: Int?,
    val eligibleFrom: LocalDate?,
    val eligibleUntil: LocalDate?,
    val commissionDecisionNumber: String?,
    val commissionDecisionDate: LocalDate?,
    val programmeAmendingDecisionNumber: String?,
    val programmeAmendingDecisionDate: LocalDate?,
    val technicalAssistanceFlatRate: Double?,
    val projectIdProgrammeAbbreviation: String?,
    val projectIdUseCallId: Boolean,
    val programmeNuts: List<OutputNuts>
) {
    fun getChange(newData: ProgrammeDataDTO): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()
        if (cci != newData.cci) {
            changes["cci"] = Pair(cci, newData.cci)
        }
        if (title != newData.title) {
            changes["title"] = Pair(title, newData.title)
        }
        if (version != newData.version) {
            changes["version"] = Pair(version, newData.version)
        }
        if (firstYear != newData.firstYear) {
            changes["firstYear"] = Pair(firstYear, newData.firstYear)
        }
        if (lastYear != newData.lastYear) {
            changes["lastYear"] = Pair(lastYear, newData.lastYear)
        }
        if (eligibleFrom != newData.eligibleFrom) {
            changes["eligibleFrom"] = Pair(eligibleFrom, newData.eligibleFrom)
        }
        if (eligibleUntil != newData.eligibleUntil) {
            changes["eligibleUntil"] = Pair(eligibleUntil, newData.eligibleUntil)
        }
        if (commissionDecisionNumber != newData.commissionDecisionNumber) {
            changes["commissionDecisionNumber"] = Pair(commissionDecisionNumber, newData.commissionDecisionNumber)
        }
        if (commissionDecisionDate != newData.commissionDecisionDate) {
            changes["commissionDecisionDate"] = Pair(commissionDecisionDate, newData.commissionDecisionDate)
        }
        if (programmeAmendingDecisionNumber != newData.programmeAmendingDecisionNumber) {
            changes["programmeAmendingDecisionNumber"] =
                Pair(programmeAmendingDecisionNumber, newData.programmeAmendingDecisionNumber)
        }
        if (programmeAmendingDecisionDate != newData.programmeAmendingDecisionDate) {
            changes["programmeAmendingDecisionDate"] =
                Pair(programmeAmendingDecisionDate, newData.programmeAmendingDecisionDate)
        }
        if (technicalAssistanceFlatRate != newData.technicalAssistanceFlatRate) {
            changes["technicalAssistanceFlatRate"] =
                Pair(technicalAssistanceFlatRate, newData.technicalAssistanceFlatRate)
        }
        if (projectIdProgrammeAbbreviation != newData.projectIdProgrammeAbbreviation) {
            changes["programme abbreviation"] =
                Pair(projectIdProgrammeAbbreviation, newData.projectIdProgrammeAbbreviation)
        }
        if (projectIdUseCallId != newData.projectIdUseCallId) {
            changes["use call id in project ID"] =
                Pair(projectIdUseCallId, newData.projectIdUseCallId)
        }

        return changes
    }

}
