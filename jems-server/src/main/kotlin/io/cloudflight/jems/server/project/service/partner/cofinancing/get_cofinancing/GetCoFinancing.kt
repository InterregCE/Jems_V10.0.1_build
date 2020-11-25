package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCoFinancing(
    private val persistence: ProjectPartnerCoFinancingPersistence
) : GetCoFinancingInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getCoFinancing(partnerId: Long): ProjectPartnerCoFinancingAndContribution =
        persistence.getCoFinancingAndContributions(partnerId)

}
