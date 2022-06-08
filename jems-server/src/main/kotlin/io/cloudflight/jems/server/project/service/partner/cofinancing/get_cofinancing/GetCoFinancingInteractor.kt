package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf

interface GetCoFinancingInteractor {

    fun getCoFinancing(partnerId: Long, version: String? = null): ProjectPartnerCoFinancingAndContribution

    fun getCoFinancingForPartnerList(partnerIds: List<Long>, projectId: Long, version: String? = null): Map<Long, List<ProjectPartnerCoFinancing>>?

    fun getSpfCoFinancing(partnerId: Long, version: String? = null): ProjectPartnerCoFinancingAndContributionSpf

}
