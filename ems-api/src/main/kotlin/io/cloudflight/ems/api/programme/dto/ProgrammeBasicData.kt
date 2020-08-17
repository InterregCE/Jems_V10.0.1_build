package io.cloudflight.ems.api.programme.dto

import io.cloudflight.ems.api.validators.StartDateBeforeEndDate
import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@StartDateBeforeEndDate("programme.lastYear.before.firstYear")
data class ProgrammeBasicData(

    @field:Size(max = 15, message = "programme.cci.size.too.long")
    val cci: String?,

    @field:Size(max = 255, message = "programme.title.size.too.long")
    val title: String?,

    @field:Size(max = 255, message = "programme.version.size.too.long")
    val version: String?,

    @field:Min(1000, message = "programme.firstYear.invalid.year")
    @field:Max(9999, message = "programme.firstYear.invalid.year")
    val firstYear: Int?,

    @field:Min(1000, message = "programme.lastYear.invalid.year")
    @field:Max(9999, message = "programme.lastYear.invalid.year")
    val lastYear: Int?,

    val eligibleFrom: LocalDate?,

    val eligibleUntil: LocalDate?,

    @field:Size(max = 255, message = "programme.commissionDecisionNumber.size.too.long")
    val commissionDecisionNumber: String?,

    val commissionDecisionDate: LocalDate?,

    @field:Size(max = 255, message = "programme.programmeAmendingDecisionNumber.size.too.long")
    val programmeAmendingDecisionNumber: String?,

    val programmeAmendingDecisionDate: LocalDate?


) {
    fun getChange(newData: ProgrammeBasicData):
        Map<String, Pair<Any?, Any?>> {
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

        return changes
    }
}
