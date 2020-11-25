package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.server.programme.service.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing

fun Collection<ProjectPartnerCoFinancingInputDTO>.toFinancingModel() = mapTo(HashSet()) {
    UpdateProjectPartnerCoFinancing(
        id = it.id ?: 0,
        fundId = it.fundId,
        percentage = it.percentage
    )
}

fun Collection<ProjectPartnerContributionDTO>.toContributionModel() = map {
    ProjectPartnerContribution(
        id = it.id ?: 0,
        name = if (it.isPartner) null else it.name,
        status = it.status,
        amount = it.amount,
        isPartner = it.isPartner
    )
}

fun ProjectPartnerCoFinancingAndContribution.toDto() = ProjectPartnerCoFinancingAndContributionOutputDTO(
    finances = finances.toCoFinancingDto(),
    partnerContributions = partnerContributions.toContributionDto(partnerAbbreviation)
)

fun ProjectPartnerCoFinancing.toDto() = ProjectPartnerCoFinancingOutputDTO(
    id = id!!,
    percentage = percentage,
    fund = fund?.toDto()
)

fun Collection<ProjectPartnerCoFinancing>.toCoFinancingDto() = map { it.toDto() }
    .sortedWith(compareBy({ it.fund == null }, { it.id }))

fun ProjectPartnerContribution.toDto(partnerAbbreviation: String) = ProjectPartnerContributionDTO(
    id = id,
    name = if (isPartner) partnerAbbreviation else name,
    status = status,
    isPartner = isPartner,
    amount = amount
)

fun Collection<ProjectPartnerContribution>.toContributionDto(partnerAbbreviation: String): List<ProjectPartnerContributionDTO> =
    if (isEmpty())
        listOf(ProjectPartnerContributionDTO(isPartner = true, name = partnerAbbreviation))
    else
        map { it.toDto(partnerAbbreviation) }.sortedWith(compareBy({ !it.isPartner }, { it.id }))
