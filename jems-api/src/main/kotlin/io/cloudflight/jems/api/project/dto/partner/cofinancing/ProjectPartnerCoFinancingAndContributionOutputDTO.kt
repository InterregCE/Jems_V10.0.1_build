package io.cloudflight.jems.api.project.dto.partner.cofinancing

data class ProjectPartnerCoFinancingAndContributionOutputDTO(

    val finances: List<ProjectPartnerCoFinancingOutputDTO> = emptyList(),
    val partnerContributions: List<ProjectPartnerContributionDTO> = emptyList()

)
