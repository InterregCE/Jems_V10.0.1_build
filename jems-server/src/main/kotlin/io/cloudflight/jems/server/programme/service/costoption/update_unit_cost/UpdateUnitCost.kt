package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
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
) : UpdateUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        val existingUnitCost = unitCost.id?.let { persistence.getUnitCost(it) }
        if (persistence.isProgrammeSetupRestricted()) {
            unitCostUpdateRestrictions(existingUnitCost = existingUnitCost, updatedUnitCost = unitCost)
        }

        validateUpdateUnitCost(unitCost)
        val saved = persistence.updateUnitCost(unitCost)

        unitCostChangedAudit(saved).logWith(audit)
        return saved
    }

    private fun unitCostChangedAudit(unitCost: ProgrammeUnitCost): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_CHANGED)
            .description("Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been changed")
            .build()
    }

    private fun unitCostUpdateRestrictions(existingUnitCost: ProgrammeUnitCost?, updatedUnitCost: ProgrammeUnitCost) {
        if (existingUnitCost?.type != updatedUnitCost.type ||
            existingUnitCost.costPerUnit != updatedUnitCost.costPerUnit ||
            existingUnitCost.isOneCostCategory != updatedUnitCost.isOneCostCategory ||
            existingUnitCost.categories != updatedUnitCost.categories
        )
            throw UpdateUnitCostWhenProgrammeSetupRestricted()
    }

}
