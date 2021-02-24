package io.cloudflight.jems.server.programme.service.priority.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.util.TreeSet

data class ProgrammePriority(
    val id: Long? = null,
    val code: String,
    val title: Set<InputTranslation> = emptySet(),
    val objective: ProgrammeObjective,
    val specificObjectives: List<ProgrammeSpecificObjective> = emptyList(),
) {
    fun getDiff(other: ProgrammePriority): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (code != other.code)
            changes["code"] = Pair(code, other.code)

        if (title != other.title)
            changes["title"] = Pair(title, other.title)

        if (objective != other.objective)
            changes["objective"] = Pair(objective, other.objective)

        val policies = specificObjectives.mapTo(TreeSet()) { it.programmeObjectivePolicy }
        val otherPolicies = other.specificObjectives.mapTo(TreeSet()) { it.programmeObjectivePolicy }

        if (policies != otherPolicies)
            changes["specificObjectives"] = Pair(policies, otherPolicies)

        return changes
    }

    fun getSpecificObjectivePolicies() = specificObjectives.mapTo(HashSet()) { it.programmeObjectivePolicy }
}
