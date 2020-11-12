package io.cloudflight.jems.server.project.entity.partner.budget

interface CommonBudget {
    val id: Long?
    val partnerId: Long
    val budget: Budget
}
