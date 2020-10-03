package io.cloudflight.jems.api.indicator.dto

import io.cloudflight.jems.api.programme.dto.ProgrammeObjectivePolicy
import java.math.BigDecimal

data class OutputIndicatorOutput (
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmePriorityPolicySpecificObjective: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val programmePriorityCode: String?,
    val measurementUnit: String?,
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?
) {
    fun getDiff(other: OutputIndicatorOutput): Map<String, Pair<Any?, Any?>> {
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
        if (programmePriorityPolicySpecificObjective != other.programmePriorityPolicySpecificObjective) {
            changes["programmePriorityPolicy"] = Pair(programmePriorityPolicySpecificObjective, other.programmePriorityPolicySpecificObjective)
        }
        if (measurementUnit != other.measurementUnit) {
            changes["measurementUnit"] = Pair(measurementUnit, other.measurementUnit)
        }
        if (milestone != other.milestone) {
            changes["milestone"] = Pair(milestone, other.milestone)
        }
        if (finalTarget != other.finalTarget) {
            changes["finalTarget"] = Pair(finalTarget, other.finalTarget)
        }
        return changes
    }
}
