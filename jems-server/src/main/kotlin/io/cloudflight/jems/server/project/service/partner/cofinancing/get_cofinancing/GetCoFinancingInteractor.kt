package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution

interface GetCoFinancingInteractor {

    fun getCoFinancing(partnerId: Long, version: String? = null): ProjectPartnerCoFinancingAndContribution

}
