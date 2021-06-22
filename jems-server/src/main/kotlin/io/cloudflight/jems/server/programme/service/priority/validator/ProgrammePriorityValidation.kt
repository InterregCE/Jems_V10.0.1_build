package io.cloudflight.jems.server.programme.service.priority.validator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import org.springframework.http.HttpStatus

fun validateCreateProgrammePriority(
    programmePriority: ProgrammePriority,
    getPriorityIdByCode: (String) -> Long?,
    getPriorityIdForPolicyIfExists: (ProgrammeObjectivePolicy) -> Long?,
    getSpecificObjectivesByCodes: (Collection<String>) -> List<ProgrammeSpecificObjective>,
) {
    validateCommonRestrictions(programmePriority)

    validateCreateHasUniqueCode(
        programmePriority = programmePriority,
        getPriorityIdByCode = getPriorityIdByCode,
    )
    validateEveryPolicyIsFree(
        policies = programmePriority.getSpecificObjectivePolicies(),
        getPriorityIdForPolicyIfExists = getPriorityIdForPolicyIfExists,
    )
    validateEveryPolicyCodeIsFree(
        policyCodes = programmePriority.specificObjectives.mapTo(HashSet()) { it.code },
        getSpecificObjectivesByCodes = getSpecificObjectivesByCodes,
    )
}

fun validateUpdateProgrammePriority(
    programmePriorityId: Long,
    programmePriority: ProgrammePriority,
    getPriorityIdByCode: (String) -> Long?,
    getPriorityIdForPolicyIfExists: (ProgrammeObjectivePolicy) -> Long?,
    getPrioritiesBySpecificObjectiveCodes: (Collection<String>) -> List<ProgrammePriority>,
) {
    validateCommonRestrictions(programmePriority)

    validateUpdateHasUniqueCode(
        priorityId = programmePriorityId,
        programmePriority = programmePriority,
        getPriorityIdByCode = getPriorityIdByCode,
    )
    validateEveryPolicyIsFreeOrLinkedToThisPriority(
        priorityId = programmePriorityId,
        policies = programmePriority.getSpecificObjectivePolicies(),
        getPriorityIdForPolicyIfExists = getPriorityIdForPolicyIfExists,
    )
    validateEveryPolicyCodeIsFreeOrLinkedToThisPriority(
        priorityId = programmePriorityId,
        policyCodes = programmePriority.specificObjectives.mapTo(HashSet()) { it.code },
        getPrioritiesBySpecificObjectiveCodes = getPrioritiesBySpecificObjectiveCodes,
    )
}

private fun validateCommonRestrictions(programmePriority: ProgrammePriority) {

    validateSpecificObjectivePoliciesAreFromCorrectProgrammeObjective(
        specificObjectives = programmePriority.specificObjectives,
        programmeObjective = programmePriority.objective,
    )
    validateSpecificObjectivesAreUnique(
        specificObjectives = programmePriority.specificObjectives
    )
}

private fun validateCreateHasUniqueCode(
    programmePriority: ProgrammePriority,
    getPriorityIdByCode: (String) -> Long?
) {
    val priorityIdWithSameCode = getPriorityIdByCode.invoke(programmePriority.code)
    if (priorityIdWithSameCode != null)
        invalid("programme.priority.code.already.in.use")
}

private fun validateUpdateHasUniqueCode(
    priorityId: Long,
    programmePriority: ProgrammePriority,
    getPriorityIdByCode: (String) -> Long?
) {
    val priorityIdWithSameCode = getPriorityIdByCode.invoke(programmePriority.code)
    if (priorityIdWithSameCode != null && priorityIdWithSameCode != priorityId)
        invalid("programme.priority.code.already.in.use")
}

private fun validateSpecificObjectivePoliciesAreFromCorrectProgrammeObjective(
    specificObjectives: Collection<ProgrammeSpecificObjective>,
    programmeObjective: ProgrammeObjective,
) {
    if (specificObjectives.any { it.programmeObjectivePolicy.objective != programmeObjective })
        invalid("programme.priority.specificObjectives.should.not.be.of.different.objectives")
}

private fun validateSpecificObjectivesAreUnique(specificObjectives: Collection<ProgrammeSpecificObjective>) {
    val codes = specificObjectives.mapTo(HashSet()) { it.code }
    val policies = specificObjectives.mapTo(HashSet()) { it.programmeObjectivePolicy }

    if (codes.size != specificObjectives.size)
        invalid("programme.priority.specificObjective.code.should.be.unique")
    if (policies.size != specificObjectives.size)
        invalid("programme.priority.specificObjective.objectivePolicy.should.be.unique")
}

private fun validateEveryPolicyIsFree(
    policies: Set<ProgrammeObjectivePolicy>,
    getPriorityIdForPolicyIfExists: (ProgrammeObjectivePolicy) -> Long?
) {
    val policiesInUse = policies.filter { getPriorityIdForPolicyIfExists.invoke(it) != null }.map { it.name }
    if (policiesInUse.isNotEmpty())
        invalid(
            fieldErrors = mapOf(
                "specificObjectives" to I18nFieldError(
                    i18nKey = "programme.priority.specificObjective.objectivePolicy.already.in.use",
                    i18nArguments = policiesInUse
                )
            )
        )
}

private fun validateEveryPolicyCodeIsFree(
    policyCodes: Set<String>,
    getSpecificObjectivesByCodes: (Collection<String>) -> List<ProgrammeSpecificObjective>
) {
    val existingPolicyCodes = getSpecificObjectivesByCodes.invoke(policyCodes).map { it.code }

    if (existingPolicyCodes.isNotEmpty())
        invalid(
            fieldErrors = mapOf(
                "specificObjectives" to I18nFieldError(
                    i18nKey = "programme.priority.specificObjective.code.already.in.use",
                    i18nArguments = existingPolicyCodes
                )
            )
        )
}

private fun validateEveryPolicyIsFreeOrLinkedToThisPriority(
    priorityId: Long,
    policies: Set<ProgrammeObjectivePolicy>,
    getPriorityIdForPolicyIfExists: (ProgrammeObjectivePolicy) -> Long?
) {
    val policiesInUse = mutableListOf<String>()

    policies.forEach {
        val priorityIdOfThisPolicy = getPriorityIdForPolicyIfExists.invoke(it)
        if (priorityIdOfThisPolicy != null && priorityIdOfThisPolicy != priorityId) {
            policiesInUse.add(it.name)
        }
    }

    if (policiesInUse.isNotEmpty())
        invalid(
            fieldErrors = mapOf(
                "specificObjectives" to I18nFieldError(
                    i18nKey = "programme.priority.specificObjective.objectivePolicy.already.in.use",
                    i18nArguments = policiesInUse
                )
            )
        )
}

private fun validateEveryPolicyCodeIsFreeOrLinkedToThisPriority(
    priorityId: Long,
    policyCodes: Set<String>,
    getPrioritiesBySpecificObjectiveCodes: (Collection<String>) -> List<ProgrammePriority>
) {
    val priorities = getPrioritiesBySpecificObjectiveCodes(policyCodes).filter {
        it.id!! != priorityId
    }.map { it.code }

    if (priorities.isNotEmpty())
        invalid(
            fieldErrors = mapOf(
                "specificObjectives" to I18nFieldError(
                    i18nKey = "programme.priority.specificObjective.code.already.in.use.by.other.priority",
                    i18nArguments = priorities
                )
            )
        )
}

fun checkNoCallExistsForRemovedSpecificObjectives(
    objectivePoliciesToBeRemoved: Set<ProgrammeObjectivePolicy>,
    alreadyUsedObjectivePolicies: Iterable<ProgrammeObjectivePolicy>
) {
    val policiesThatCannotBeRemoved = alreadyUsedObjectivePolicies intersect objectivePoliciesToBeRemoved
    if (policiesThatCannotBeRemoved.isNotEmpty())
        invalid(fieldErrors = mapOf(
            "specificObjectives" to I18nFieldError(
                i18nKey = "programme.priority.specificObjective.already.used.in.call",
                i18nArguments = policiesThatCannotBeRemoved.map { it.name }
            )
        ))
}

private fun invalid(message: String? = null, fieldErrors: Map<String, I18nFieldError> = emptyMap()) {
    throw I18nValidationException(
        httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        i18nKey = message,
        i18nFieldErrors = fieldErrors
    )
}
