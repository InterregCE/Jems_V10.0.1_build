package io.cloudflight.jems.server.programme.service.priority.create_priority

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.validator.validateCreateProgrammePriority
import io.cloudflight.jems.server.programme.service.programmePriorityAdded
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePriority(
    private val persistence: ProgrammePriorityPersistence,
    private val auditService: AuditService,
    private val generalValidatorService: GeneralValidatorService
) : CreatePriorityInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    override fun createPriority(priority: ProgrammePriority): ProgrammePriority {
        validateProgrammePriority(priority)
        validateCreateProgrammePriority(
            programmePriority = priority,
            getPriorityIdByCode = { persistence.getPriorityIdByCode(it) },
            getPriorityIdForPolicyIfExists = { persistence.getPriorityIdForPolicyIfExists(it) },
            getSpecificObjectivesByCodes = { persistence.getSpecificObjectivesByCodes(it) }
        )
        val newPriority = persistence.create(priority)
        programmePriorityAdded(newPriority).logWith(auditService)
        return newPriority
    }

    private fun validateProgrammePriority(programmePriority: ProgrammePriority) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.notBlank(programmePriority.code, "code"),
            generalValidatorService.maxLength(programmePriority.code,50, "code"),
            generalValidatorService.maxLength(programmePriority.title,300, "title"),
            generalValidatorService.minSize(programmePriority.specificObjectives,1, "specificObjectives"),
            programmePriority.specificObjectives.map {
                generalValidatorService.notBlank(it.code, "specificObjectives")
            }.firstOrNull { it.isNotEmpty() } ?: mapOf(),

            programmePriority.specificObjectives.map {
                generalValidatorService.maxLength(it.code, 50,"specificObjectives")
            }.firstOrNull { it.isNotEmpty() } ?: mapOf()
        )
    }
}
