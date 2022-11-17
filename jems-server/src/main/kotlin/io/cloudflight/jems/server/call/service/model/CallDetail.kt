package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import java.time.ZonedDateTime
import java.util.SortedSet
import java.util.TreeSet

data class CallDetail(
    val id: Long,
    val name: String,
    val status: CallStatus,
    val type: CallType,
    val startDate: ZonedDateTime,
    val endDateStep1: ZonedDateTime?,
    val endDate: ZonedDateTime,
    val isAdditionalFundAllowed: Boolean,
    val lengthOfPeriod: Int?,
    val description: Set<InputTranslation> = emptySet(),
    val objectives: List<ProgrammePriority> = emptyList(),
    val strategies: SortedSet<ProgrammeStrategy> = sortedSetOf(),
    val funds: SortedSet<CallFundRate> = sortedSetOf(),
    val stateAids: List<ProgrammeStateAid> = emptyList(),
    val flatRates: SortedSet<ProjectCallFlatRate> = sortedSetOf(),
    val lumpSums: List<ProgrammeLumpSum> = emptyList(),
    val unitCosts: List<ProgrammeUnitCost> = emptyList(),
    val applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>,
    val preSubmissionCheckPluginKey: String?,
    val firstStepPreSubmissionCheckPluginKey: String?,
    val projectDefinedUnitCostAllowed: Boolean,
    val projectDefinedLumpSumAllowed: Boolean,
) {
    fun isPublished() = status == CallStatus.PUBLISHED

    fun getCallApplyDeadline(): ZonedDateTime = endDateStep1 ?: endDate

    fun is2StepCall(): Boolean = endDateStep1 != null

    fun getAllSpecificObjectives() = objectives
        .map { it.specificObjectives.map { it.programmeObjectivePolicy }.toSet() }
        .takeIf { it.isNotEmpty() }
        ?.reduce { first, second -> first union second }
        ?.toSortedSet()
        ?: sortedSetOf()

    fun getDiff(old: CallDetail? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || name != old.name)
            changes["name"] = Pair(old?.name, name)

        if (old == null || status != old.status)
            changes["status"] = Pair(old?.status, status)

        if (old == null || startDate.toInstant() != old.startDate.toInstant())
            changes["startDate"] = Pair(old?.startDate, startDate)

        if (old == null || endDateStep1?.toInstant() != old.endDateStep1?.toInstant())
            changes["endDateStep1"] = Pair(old?.endDateStep1, endDateStep1)

        if (old == null || endDate.toInstant() != old.endDate.toInstant())
            changes["endDate"] = Pair(old?.endDate, endDate)

        if (old == null || isAdditionalFundAllowed != old.isAdditionalFundAllowed)
            changes["isAdditionalFundAllowed"] = Pair(old?.isAdditionalFundAllowed, isAdditionalFundAllowed)

        if (old == null || lengthOfPeriod != old.lengthOfPeriod)
            changes["lengthOfPeriod"] = Pair(old?.lengthOfPeriod, lengthOfPeriod)

        if (description != (old?.description ?: emptySet<InputTranslation>()))
            changes["description"] = Pair(old?.description, description)

        val oldSpecificObjectives = old?.getAllSpecificObjectives() ?: sortedSetOf()
        val newSpecificObjectives = getAllSpecificObjectives()
        if (newSpecificObjectives != oldSpecificObjectives)
            changes["objectives"] = Pair(oldSpecificObjectives, newSpecificObjectives)

        if (strategies != (old?.strategies ?: sortedSetOf<InputTranslation>()))
            changes["strategies"] = Pair(old?.strategies, strategies)

        val oldFundIds = old?.funds?.mapTo(TreeSet()) { it.programmeFund.id } ?: sortedSetOf()
        val newFundIds = funds.mapTo(TreeSet()) { it.programmeFund.id }
        if (newFundIds != oldFundIds)
            changes["fundIds"] = Pair(oldFundIds, newFundIds)

        if (funds != (old?.funds ?: sortedSetOf<CallFundRate>()))
            changes["funds"] = Pair(old?.funds, funds)

        if (flatRates != (old?.flatRates ?: sortedSetOf<ProjectCallFlatRate>()))
            changes["flatRates"] = Pair(old?.flatRates, flatRates)

        val oldLumpSumIds = old?.lumpSums?.mapTo(TreeSet()) { it.id } ?: sortedSetOf()
        val newLumpSumIds = lumpSums.mapTo(TreeSet()) { it.id }
        if (newLumpSumIds != oldLumpSumIds)
            changes["lumpSumIds"] = Pair(oldLumpSumIds, newLumpSumIds)

        val oldUnitCostIds = old?.unitCosts?.mapTo(TreeSet()) { it.id } ?: sortedSetOf()
        val newUnitCostIds = unitCosts.mapTo(TreeSet()) { it.id }
        if (newUnitCostIds != oldUnitCostIds)
            changes["unitCostIds"] = Pair(oldUnitCostIds, newUnitCostIds)

        if (old == null || preSubmissionCheckPluginKey != old.preSubmissionCheckPluginKey)
            changes["preSubmissionCheckPluginKey"] = Pair(old?.preSubmissionCheckPluginKey, preSubmissionCheckPluginKey)

        return changes
    }
}
