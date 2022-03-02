package io.cloudflight.jems.server.project.service.partner.cofinancing.model

data class ProjectPartnerCoFinancingAndContributionSpf(

    val finances: List<ProjectPartnerCoFinancing>,
    val partnerContributions: Collection<ProjectPartnerContributionSpf>

)
