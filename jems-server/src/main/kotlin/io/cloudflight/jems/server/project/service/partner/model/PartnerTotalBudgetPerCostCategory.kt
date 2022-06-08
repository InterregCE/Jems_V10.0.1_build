package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

data class PartnerTotalBudgetPerCostCategory(
    val partnerId: Long,
    val officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
    val officeAndAdministrationOnDirectCostsFlatRate: Int? = null,
    val travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
    val staffCostsFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null,
    val unitCostTotal: BigDecimal,
    val equipmentCostTotal: BigDecimal,
    val externalCostTotal: BigDecimal,
    val infrastructureCostTotal: BigDecimal,
    val travelCostTotal: BigDecimal,
    val staffCostTotal: BigDecimal,
    val lumpSumsTotal: BigDecimal,
)
