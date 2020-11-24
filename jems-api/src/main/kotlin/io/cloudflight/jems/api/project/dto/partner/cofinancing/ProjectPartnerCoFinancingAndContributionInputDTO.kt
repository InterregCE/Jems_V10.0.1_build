package io.cloudflight.jems.api.project.dto.partner.cofinancing

data class ProjectPartnerCoFinancingAndContributionInputDTO(

    val finances: List<ProjectPartnerCoFinancingInputDTO> = emptyList(),
    val partnerContributions: List<ProjectPartnerContributionDTO> = emptyList()

)
