package io.cloudflight.jems.server.project.service.partner.cofinancing.model

data class ProjectPartnerCoFinancingAndContribution(

    val finances: List<ProjectPartnerCoFinancing>,
    val partnerContributions: Collection<ProjectPartnerContribution>,
    val partnerAbbreviation: String

)
