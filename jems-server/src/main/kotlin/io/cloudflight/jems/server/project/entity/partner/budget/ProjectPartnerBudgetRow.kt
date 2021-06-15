package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

interface ProjectPartnerBudgetRow {
    val partnerId: Long
    val sum: BigDecimal
}