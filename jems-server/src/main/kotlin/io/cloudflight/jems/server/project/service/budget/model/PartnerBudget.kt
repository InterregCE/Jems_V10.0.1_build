package io.cloudflight.jems.server.project.service.budget.model

import io.cloudflight.jems.server.project.service.partner.budget.percentage
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal

data class PartnerBudget(

    val partner: ProjectPartner? = null,

    val staffCostsFlatRate: Int? = null,
    val officeOnStaffFlatRate: Int? = null,

    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val externalCosts: BigDecimal = BigDecimal.ZERO,
    val equipmentCosts: BigDecimal = BigDecimal.ZERO,
    val infrastructureCosts: BigDecimal = BigDecimal.ZERO

) {
    fun extractStaffCosts(): BigDecimal =
        if (staffCostsFlatRate != null)
            travelCosts.add(externalCosts).add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)
        else
            staffCosts

    fun extractOfficeAndAdministrationCosts(): BigDecimal =
        if (officeOnStaffFlatRate != null)
            extractStaffCosts().percentage(officeOnStaffFlatRate)
        else
            BigDecimal.ZERO

    fun totalSum(): BigDecimal =
        extractStaffCosts().add(extractOfficeAndAdministrationCosts())
            .add(travelCosts)
            .add(externalCosts)
            .add(equipmentCosts)
            .add(infrastructureCosts)
}
