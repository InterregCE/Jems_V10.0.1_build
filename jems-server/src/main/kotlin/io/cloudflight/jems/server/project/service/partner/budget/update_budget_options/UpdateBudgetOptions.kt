package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectBudgetPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetOptions(
    private val persistence: ProjectBudgetPersistence,
    private val callFlatRateSetupPersistence: CallFlatRateSetupPersistence
) : UpdateBudgetOptionsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?) {
        val callFlatRateSetup = callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId)

        validateFlatRates(
            callFlatRateSetup = callFlatRateSetup,
            staffCostsFlatRate = staffCostsFlatRate,
            officeAdministrationFlatRate = officeAdministrationFlatRate
        )

        if (officeAdministrationFlatRate == null && staffCostsFlatRate == null) persistence.deleteBudgetOptions(partnerId)
        else {
            if (staffCostsFlatRate != null) persistence.deleteStaffCosts(partnerId)
            persistence.updateBudgetOptions(partnerId, officeAdministrationFlatRate, staffCostsFlatRate)
        }
    }
}
