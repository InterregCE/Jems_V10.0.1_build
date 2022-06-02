package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
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
    private val persistence: ProjectPartnerCoFinancingPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val callPersistence: CallPersistence
) : UpdateCoFinancingInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateCoFinancing(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val callId = projectPersistence.getCallIdOfProject(projectId)
        val afConfig = callPersistence.getApplicationFormFieldConfigurations(callId)

        validateContributionAFConfiguration(afConfig.applicationFormFieldConfigurations, partnerContributions)
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
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val callId = projectPersistence.getCallIdOfProject(projectId)
        val afConfig = callPersistence.getApplicationFormFieldConfigurations(callId)

        validateSpfContributionAFConfiguration(afConfig.applicationFormFieldConfigurations, partnerContributions)
        validateFinancing(finances, persistence.getAvailableFunds(partnerId).map { it.id }.toSet())
        validateContributionSpf(partnerContributions)

        return persistence.updateSpfCoFinancingAndContribution(partnerId, finances, partnerContributions)
    }
}
