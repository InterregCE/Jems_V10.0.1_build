package io.cloudflight.jems.server.project.service.partner.cofinancing.model

data class ProjectPartnerCoFinancingAndContribution(

    override val finances: List<ProjectPartnerCoFinancing>,
    override val partnerContributions: Collection<ProjectPartnerContribution>,
    val partnerAbbreviation: String

) : ProjectCoFinancingAndContribution
