package io.cloudflight.jems.server.programme.service.costoption.create_unit_cost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.MAX_ALLOWED_UNIT_COSTS
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.getStaticValidationResults
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.validateCreateUnitCost
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
    private val audit: AuditService,
    private val generalValidator: GeneralValidatorService,
) : CreateUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    override fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        validateInput(unitCost)
        validateCreateUnitCost(unitCostToValidate = unitCost, currentCount = persistence.getCount(), MAX_ALLOWED_UNIT_COSTS)
        val saved = persistence.createUnitCost(unitCost)

        unitCostCreatedAudit(saved).logWith(audit)
        return saved
    }

    private fun unitCostCreatedAudit(unitCost: ProgrammeUnitCost): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_ADDED)
            .description("Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been added")
            .build()
    }

    private fun validateInput(programmeUnitCost: ProgrammeUnitCost) =
        generalValidator.throwIfAnyIsInvalid(
            *programmeUnitCost.getStaticValidationResults(generalValidator).toTypedArray(),
        )
}
