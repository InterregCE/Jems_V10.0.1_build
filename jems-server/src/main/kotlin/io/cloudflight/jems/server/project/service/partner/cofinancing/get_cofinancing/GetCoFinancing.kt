package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCoFinancing(
    private val persistence: ProjectPartnerCoFinancingPersistence
) : GetCoFinancingInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getCoFinancing(partnerId: Long, version: String?): ProjectPartnerCoFinancingAndContribution =
        persistence.getCoFinancingAndContributions(partnerId, version)

}
