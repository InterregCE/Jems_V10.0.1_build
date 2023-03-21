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

    val staffAfterControl: BigDecimal,
    val officeAfterControl: BigDecimal,
    val travelAfterControl: BigDecimal,
    val externalAfterControl: BigDecimal,
    val equipmentAfterControl: BigDecimal,
    val infrastructureAfterControl: BigDecimal,
    val otherAfterControl: BigDecimal,
    val lumpSumAfterControl: BigDecimal,
    val unitCostAfterControl: BigDecimal,
    val sumAfterControl: BigDecimal,
)
