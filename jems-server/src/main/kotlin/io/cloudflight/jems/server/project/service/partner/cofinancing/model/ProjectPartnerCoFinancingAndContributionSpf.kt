package io.cloudflight.jems.server.project.service.partner.cofinancing.model

data class ProjectPartnerCoFinancingAndContributionSpf(

    override val finances: List<ProjectPartnerCoFinancing>,
    override val partnerContributions: Collection<ProjectPartnerContributionSpf>

) : ProjectCoFinancingAndContribution
