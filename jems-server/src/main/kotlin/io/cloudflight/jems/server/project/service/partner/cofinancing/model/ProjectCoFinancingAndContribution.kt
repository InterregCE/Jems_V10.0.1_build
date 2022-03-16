package io.cloudflight.jems.server.project.service.partner.cofinancing.model

interface ProjectCoFinancingAndContribution {
    val finances: List<ProjectPartnerCoFinancing>
    val partnerContributions: Collection<ProjectContribution>
}
