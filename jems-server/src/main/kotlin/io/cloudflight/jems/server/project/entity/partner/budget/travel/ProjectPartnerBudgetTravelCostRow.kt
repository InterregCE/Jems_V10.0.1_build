package io.cloudflight.jems.server.project.entity.partner.budget.travel

import io.cloudflight.jems.server.common.entity.TranslationView
import org.springframework.beans.factory.annotation.Value
import java.math.BigDecimal

interface ProjectPartnerBudgetTravelCostRow: TranslationView {
    fun getId(): Long

    @Value("#{target.partner_id}")
    fun getPartnerId(): Long

    @Value("#{target.period_number}")
    fun getPeriodNumber(): Int?

    fun getAmount(): BigDecimal

    @Value("#{target.number_of_units}")
    fun getNumberOfUnits(): BigDecimal

    @Value("#{target.price_per_unit}")
    fun getPricePerUnit(): BigDecimal

    @Value("#{target.row_sum}")
    fun getRowSum(): BigDecimal

    @Value("#{target.unit_cost_id}")
    fun getUnitCostId(): Long?

    fun getDescription(): String?

    @Value("#{target.unit_type}")
    fun getUnitType(): String?
}
