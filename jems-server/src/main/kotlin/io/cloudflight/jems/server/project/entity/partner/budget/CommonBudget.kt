package io.cloudflight.jems.server.project.entity.partner.budget

interface CommonBudget {
    val id: Long?
    val partnerId: Long
    // TODO remove this nullable modifier (?) from type when fixed in cloudflight cleancode verifier
    val budget: Budget?
}
