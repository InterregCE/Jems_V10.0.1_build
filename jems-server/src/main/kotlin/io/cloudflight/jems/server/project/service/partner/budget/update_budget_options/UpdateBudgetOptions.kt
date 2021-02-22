package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetOptions(
    private val persistence: ProjectPartnerBudgetOptionsPersistence,
    private val callFlatRateSetupPersistence: CallFlatRateSetupPersistence
) : UpdateBudgetOptionsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    @ExceptionWrapper(UpdateBudgetOptionsException::class)
    override fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions) {

        validateFlatRatesCombinations(options)

        val callFlatRateSetup = callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId)

        validateFlatRates(
            callFlatRateSetup = callFlatRateSetup,
            options = options,
        )

        if (options.isEmpty())
            return persistence.deleteBudgetOptions(partnerId)

        if (options.otherCostsOnStaffCostsFlatRate != null) {
            persistence.deleteTravelAndAccommodationCosts(partnerId)
            persistence.deleteEquipmentCosts(partnerId)
            persistence.deleteExternalCosts(partnerId)
            persistence.deleteInfrastructureCosts(partnerId)
            persistence.deleteUnitCosts(partnerId)
        }
        if (options.staffCostsFlatRate != null)
            persistence.deleteStaffCosts(partnerId)

        if (options.travelAndAccommodationOnStaffCostsFlatRate != null)
            persistence.deleteTravelAndAccommodationCosts(partnerId)

        persistence.updateBudgetOptions(partnerId, options)
    }

}
