package io.cloudflight.jems.server.project.entity.partner.budget.unit_cost

import org.springframework.beans.factory.annotation.Value
import java.math.BigDecimal

interface ProjectPartnerBudgetUnitCostRow {
    fun getId(): Long

    @Value("#{target.partner_id}")
    fun getPartnerId(): Long

    @Value("#{target.period_number}")
    fun getPeriodNumber(): Int?

    fun getAmount(): BigDecimal

    @Value("#{target.number_of_units}")
    fun getNumberOfUnits(): BigDecimal

    @Value("#{target.row_sum}")
    fun getRowSum(): BigDecimal

    @Value("#{target.programme_unit_cost_id}")
    fun getUnitCostId(): Long

}
