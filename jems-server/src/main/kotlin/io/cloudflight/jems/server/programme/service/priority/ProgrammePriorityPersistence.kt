package io.cloudflight.jems.server.programme.service.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective

interface ProgrammePriorityPersistence {
    fun getPriorityById(priorityId: Long): ProgrammePriority
    fun getAllMax45Priorities(): List<ProgrammePriority>
    fun create(priority: ProgrammePriority): ProgrammePriority
    fun update(priority: ProgrammePriority): ProgrammePriority
    fun delete(priorityId: Long)

    fun getPriorityIdByCode(code: String): Long?
    fun getPriorityIdForPolicyIfExists(policy: ProgrammeObjectivePolicy): Long?
    fun getSpecificObjectivesByCodes(specificObjectiveCodes: Collection<String>): List<ProgrammeSpecificObjective>
    fun getPrioritiesBySpecificObjectiveCodes(specificObjectiveCodes: Collection<String>): List<ProgrammePriority>

    fun getObjectivePoliciesAlreadySetUp(): Iterable<ProgrammeObjectivePolicy>
    fun getObjectivePoliciesAlreadyInUse(): Iterable<ProgrammeObjectivePolicy>
}
