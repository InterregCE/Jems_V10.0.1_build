package io.cloudflight.jems.server.project.repository.report.partner.model

import java.math.BigDecimal

data class PerPartnerCertificateCostCategory(
    val partnerId: Long,

    val officeAndAdministrationOnStaffCostsFlatRate: Int?,
    val officeAndAdministrationOnDirectCostsFlatRate: Int?,
    val travelAndAccommodationOnStaffCostsFlatRate: Int?,
    val staffCostsFlatRate: Int?,
    val otherCostsOnStaffCostsFlatRate: Int?,

    val staffCurrent: BigDecimal,
    val officeCurrent: BigDecimal,
    val travelCurrent: BigDecimal,
    val externalCurrent: BigDecimal,
    val equipmentCurrent: BigDecimal,
    val infrastructureCurrent: BigDecimal,
    val otherCurrent: BigDecimal,
    val lumpSumCurrent: BigDecimal,
    val unitCostCurrent: BigDecimal,
    val sumCurrent: BigDecimal,

    val staffDeduction: BigDecimal,
    val officeDeduction: BigDecimal,
    val travelDeduction: BigDecimal,
    val externalDeduction: BigDecimal,
    val equipmentDeduction: BigDecimal,
    val infrastructureDeduction: BigDecimal,
    val otherDeduction: BigDecimal,
    val lumpSumDeduction: BigDecimal,
    val unitCostDeduction: BigDecimal,
    val sumDeduction: BigDecimal,
)
