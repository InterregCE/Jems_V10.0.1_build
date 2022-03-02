package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCoFinancing(
    private val persistence: ProjectPartnerCoFinancingPersistence
) : UpdateCoFinancingInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateCoFinancing(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution {

        validateFinancing(finances, persistence.getAvailableFunds(partnerId).map { it.id }.toSet())
        validateContribution(partnerContributions)

        return persistence.updateCoFinancingAndContribution(partnerId, finances, partnerContributions)
    }

    @Transactional
    @CanUpdateProjectPartner
    override fun updateSpfCoFinancing(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContributionSpf>
    ): ProjectPartnerCoFinancingAndContributionSpf {

        validateFinancing(finances, persistence.getAvailableFunds(partnerId).map { it.id }.toSet())
        validateContributionSpf(partnerContributions)

        return persistence.updateSpfCoFinancingAndContribution(partnerId, finances, partnerContributions)
    }
}
