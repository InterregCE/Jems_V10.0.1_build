package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ResultIndicatorDetail(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val programmePriorityCode: String?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val baseline: BigDecimal?,
    val referenceYear: String?,
    val finalTarget: BigDecimal?,
    val sourceOfData: Set<InputTranslation> = emptySet(),
    val comment: String?
) {
    fun getDiff(other: ResultIndicatorDetail): Map<String, Pair<Any?, Any?>> {
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
            changes["programmeObjectivePolicy"] =
                Pair(programmeObjectivePolicy, other.programmeObjectivePolicy)
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
