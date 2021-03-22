package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateUnitCostWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.validateUpdateUnitCost
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
    private val audit: AuditService,
    private val generalValidator: GeneralValidatorService,
) : UpdateUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateUnitCostException::class)
    override fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        validateInput(unitCost)

        validateUpdateUnitCost(unitCost)

        val existingUnitCost  = persistence.getUnitCost(unitCostId = unitCost.id)
        if (persistence.isProgrammeSetupRestricted()) {
            unitCostUpdateRestrictions(existingUnitCost = existingUnitCost, updatedUnitCost = unitCost)
        }
        val saved = persistence.updateUnitCost(unitCost)

        unitCostChangedAudit(saved).logWith(audit)
        return saved
    }

    private fun unitCostChangedAudit(unitCost: ProgrammeUnitCost): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_CHANGED)
            .description("Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been changed")
            .build()
    }

    private fun unitCostUpdateRestrictions(existingUnitCost: ProgrammeUnitCost, updatedUnitCost: ProgrammeUnitCost) {
        if (!updatedUnitCost.type.containsAll(existingUnitCost.type) ||
            existingUnitCost.costPerUnit?.compareTo( updatedUnitCost.costPerUnit) != 0 ||
            existingUnitCost.isOneCostCategory != updatedUnitCost.isOneCostCategory ||
            !updatedUnitCost.categories.containsAll(existingUnitCost.categories )
        )
            throw UpdateUnitCostWhenProgrammeSetupRestricted()
    }

    private fun validateInput(programmeUnitCost: ProgrammeUnitCost) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(programmeUnitCost.id, "id"),
    )

}
