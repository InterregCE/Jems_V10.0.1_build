package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

interface ProjectPartnerTotalBudgetEntry{
    val partnerId: Long
    val officeAndAdministrationOnStaffCostsFlatRate: Int?
    val officeAndAdministrationOnDirectCostsFlatRate: Int?
    val travelAndAccommodationOnStaffCostsFlatRate: Int?
    val staffCostsFlatRate: Int?
    val otherCostsOnStaffCostsFlatRate: Int?
    val unitCostTotal: BigDecimal?
    val equipmentCostTotal: BigDecimal?
    val externalCostTotal: BigDecimal?
    val infrastructureCostTotal: BigDecimal?
    val travelCostTotal: BigDecimal?
    val staffCostTotal: BigDecimal?
    val lumpSumsTotal: BigDecimal?
}
