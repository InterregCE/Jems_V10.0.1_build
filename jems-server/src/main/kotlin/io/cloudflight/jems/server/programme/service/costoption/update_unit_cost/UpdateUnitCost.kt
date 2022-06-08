package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateUnitCostWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.validateUpdateUnitCost
import io.cloudflight.jems.server.programme.service.info.hasProjectsInStatus.HasProjectsInStatusInteractor
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.unitCostChangedAudit
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val hasProjectsInStatus: HasProjectsInStatusInteractor,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
) : UpdateUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateUnitCostException::class)
    override fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        validateInput(unitCost)
        validateUpdateUnitCost(unitCost)
        val existingUnitCost  = persistence.getUnitCost(unitCostId = unitCost.id)
        checkIfUnitCostCanBeUpdated(existingUnitCost = existingUnitCost, updatedUnitCost = unitCost)

        return persistence.updateUnitCost(unitCost).also {
            auditPublisher.publishEvent(unitCostChangedAudit(this, it))
        }
    }

    private fun checkIfUnitCostCanBeUpdated(existingUnitCost: ProgrammeUnitCost, updatedUnitCost: ProgrammeUnitCost){
        if (isProgrammeSetupLocked.isLocked()) {
            unitCostUpdateRestrictions(existingUnitCost, updatedUnitCost)
        }
        if (hasProjectsInStatus.programmeHasProjectsInStatus(ApplicationStatusDTO.CONTRACTED)) {
            updateCostInForeignCurrencyRestrictions(existingUnitCost, updatedUnitCost)
        }
    }

    private fun unitCostUpdateRestrictions(existingUnitCost: ProgrammeUnitCost, updatedUnitCost: ProgrammeUnitCost) {
        if (existingUnitCost.costPerUnit?.compareTo(updatedUnitCost.costPerUnit) != 0 ||
            existingUnitCost.isOneCostCategory != updatedUnitCost.isOneCostCategory ||
            !updatedUnitCost.categories.containsAll(existingUnitCost.categories)
        )
            throw UpdateUnitCostWhenProgrammeSetupRestricted()
    }

    private fun updateCostInForeignCurrencyRestrictions(existingUnitCost: ProgrammeUnitCost, updatedUnitCost: ProgrammeUnitCost) {
        val existingCostPerUnitForeignCurrency = existingUnitCost.costPerUnitForeignCurrency ?: BigDecimal.ZERO
        val updatedCostPerUnitForeignCurrency = updatedUnitCost.costPerUnitForeignCurrency ?: BigDecimal.ZERO
        if (existingCostPerUnitForeignCurrency.compareTo(updatedCostPerUnitForeignCurrency) != 0 ||
            existingUnitCost.foreignCurrencyCode != updatedUnitCost.foreignCurrencyCode
        )
            throw UpdateUnitCostWhenProgrammeSetupRestricted()
    }

    private fun validateInput(programmeUnitCost: ProgrammeUnitCost) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(programmeUnitCost.id, "id"),
            generalValidator.maxLength(programmeUnitCost.name, 50, "name"),
            generalValidator.maxLength(programmeUnitCost.description, 255, "description"),
            generalValidator.maxLength(programmeUnitCost.type, 25, "type"),
    )

}
