package io.cloudflight.ems.api.programme.dto

import java.time.LocalDate
import java.util.stream.Collectors

data class OutputProgrammeData(

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
    val systemLanguageSelections: List<SystemLanguageSelection>,
    val programmeNuts: Any
) {
    fun getChange(newData: OutputProgrammeData): Map<String, Pair<Any?, Any?>> {
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
        if (getSystemLanguageSelectionsAsString() != newData.getSystemLanguageSelectionsAsString()) {
            changes["languagesSystem"] =
                Pair(getSystemLanguageSelectionsAsString(), newData.getSystemLanguageSelectionsAsString())
        }

        return changes
    }

    private fun getSystemLanguageSelectionsAsString(): String {
        return systemLanguageSelections.stream()
            .filter { it.selected }
            .map(SystemLanguageSelection::name)
            .collect(Collectors.joining(","))
    }
}
