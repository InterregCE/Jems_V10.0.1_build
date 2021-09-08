package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class OutputIndicatorDetail(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val programmePriorityCode: String?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?,
    val resultIndicatorDetail: ResultIndicatorDetail?,
) {
    fun getDiff(other: OutputIndicatorDetail): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()
        if (identifier != other.identifier) {
            changes["identifier"] = Pair(identifier, other.identifier)
        }
        if (code != other.code) {
            changes["code"] = Pair(code, other.code)
        }
        if (name != other.name) {
            changes["name"] = Pair(name, other.name)
        }
        if (programmeObjectivePolicy != other.programmeObjectivePolicy) {
            changes["programmeObjectivePolicy"] = Pair(programmeObjectivePolicy, other.programmeObjectivePolicy)
        }
        if (measurementUnit != other.measurementUnit) {
            changes["measurementUnit"] = Pair(measurementUnit, other.measurementUnit)
        }
        if (milestone != other.milestone) {
            changes["milestone"] = Pair(milestone, other.milestone)
        }
        if ((finalTarget ?: BigDecimal.ZERO).compareTo(other.finalTarget) != 0) {
            changes["finalTarget"] = Pair(finalTarget, other.finalTarget)
        }
        if (resultIndicatorDetail != other.resultIndicatorDetail) {
            changes["resultIndicator"] = Pair(resultIndicatorDetail, other.resultIndicatorDetail)
        }
        return changes
    }
}
