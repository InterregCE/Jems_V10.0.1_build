package io.cloudflight.jems.server.project.service.budget.model

import io.cloudflight.jems.server.project.service.partner.budget.percentage
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal

data class PartnerBudget(

    val partner: ProjectPartner? = null,

    val staffCostsFlatRate: Int?,
    val officeAndAdministrationOnStaffCostsFlatRate: Int?,
    val travelAndAccommodationOnStaffCostsFlatRate: Int?,
    val otherCostsOnStaffCostsFlatRate: Int?,

    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val externalCosts: BigDecimal = BigDecimal.ZERO,
    val equipmentCosts: BigDecimal = BigDecimal.ZERO,
    val infrastructureCosts: BigDecimal = BigDecimal.ZERO,

    val lumpSumContribution: BigDecimal = BigDecimal.ZERO,

) {
    fun extractStaffCosts(): BigDecimal =
        if (staffCostsFlatRate != null) {
            if (travelAndAccommodationOnStaffCostsFlatRate != null)
                externalCosts.add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)
            else
                travelCosts.add(externalCosts).add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)
        }
        else
            staffCosts

    fun extractTravelCosts(): BigDecimal =
        if (travelAndAccommodationOnStaffCostsFlatRate != null)
            extractStaffCosts().percentage(travelAndAccommodationOnStaffCostsFlatRate)
        else
            travelCosts

    fun extractOfficeAndAdministrationCosts(): BigDecimal =
        if (officeAndAdministrationOnStaffCostsFlatRate != null)
            extractStaffCosts().percentage(officeAndAdministrationOnStaffCostsFlatRate)
        else
            BigDecimal.ZERO

    fun extractOtherCosts(): BigDecimal =
        if (otherCostsOnStaffCostsFlatRate != null) {
            if (staffCostsFlatRate == null)
                extractStaffCosts().percentage(otherCostsOnStaffCostsFlatRate)
            else
                BigDecimal.ZERO
        }
        else
            BigDecimal.ZERO


    fun totalSum(): BigDecimal =
        extractStaffCosts()
            .add(extractTravelCosts())
            .add(externalCosts)
            .add(equipmentCosts)
            .add(infrastructureCosts)
            .add(extractOfficeAndAdministrationCosts())
            .add(extractOtherCosts())
            .add(lumpSumContribution)

}
