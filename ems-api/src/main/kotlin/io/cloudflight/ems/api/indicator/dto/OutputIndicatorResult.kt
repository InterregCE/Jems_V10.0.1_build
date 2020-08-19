package io.cloudflight.ems.api.indicator.dto

import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import java.math.BigDecimal

data class OutputIndicatorResult (
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmePriorityPolicySpecificObjective: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val programmePriorityCode: String?,
    val measurementUnit: String?,
    val baseline: BigDecimal?,
    val referenceYear: String?,
    val finalTarget: BigDecimal?,
    val sourceOfData: String?,
    val comment: String?
) {
    fun getDiff(other: OutputIndicatorResult): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()
        if (identifier != other.identifier) {
            changes["id"] = Pair(identifier, other.identifier)
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
        if (baseline != other.baseline) {
            changes["baseline"] = Pair(baseline, other.baseline)
        }
        if (referenceYear != other.referenceYear) {
            changes["referenceYear"] = Pair(referenceYear, other.referenceYear)
        }
        if (finalTarget != other.finalTarget) {
            changes["finalTarget"] = Pair(finalTarget, other.finalTarget)
        }
        if (sourceOfData != other.sourceOfData) {
            changes["sourceOfData"] = Pair(sourceOfData, other.sourceOfData)
        }
        if (comment != other.comment) {
            changes["comment"] = Pair(comment, other.comment)
        }
        return changes
    }
}
