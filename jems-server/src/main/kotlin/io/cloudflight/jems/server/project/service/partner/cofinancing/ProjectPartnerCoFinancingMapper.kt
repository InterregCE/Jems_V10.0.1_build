package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.cofinancing.ProjectCoFinancingByFundOverviewDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectCoFinancingOverviewDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectPartnerBudgetCoFinancingDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.server.common.CommonDTOMapper
import io.cloudflight.jems.server.notification.mail.entity.MailNotificationEntity
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerCoFinancingMapper::class.java)

fun ProjectCoFinancingOverview.toDto() =
    mapper.map(this)

fun ProjectCoFinancingByFundOverview.toDto() =
    mapper.map(this)

@Mapper(uses = [CommonDTOMapper::class])
abstract class ProjectPartnerCoFinancingMapper {
    abstract fun map(coFinancingOverview: ProjectCoFinancingOverview): ProjectCoFinancingOverviewDTO
    abstract fun map(coFinancingByFundOverview: ProjectCoFinancingByFundOverview): ProjectCoFinancingByFundOverviewDTO
}

fun List<ProjectPartnerCoFinancingInputDTO>.toFinancingModel() = map {
    UpdateProjectPartnerCoFinancing(
        fundId = it.fundId,
        percentage = it.percentage
    )
}

fun Collection<ProjectPartnerContributionDTO>.toContributionModel() = map {
    ProjectPartnerContribution(
        id = it.id ?: 0,
        name = if (it.partner) null else it.name,
        status = it.status,
        amount = it.amount,
        isPartner = it.partner
    )
}

fun ProjectPartnerCoFinancingAndContribution.toDto() = ProjectPartnerCoFinancingAndContributionOutputDTO(
    finances = finances.toCoFinancingDto(),
    partnerContributions = partnerContributions.toContributionDto(partnerAbbreviation)
)

fun ProjectPartnerCoFinancing.toDto() = ProjectPartnerCoFinancingOutputDTO(
    fundType = fundType,
    percentage = percentage,
    fund = fund?.toDto()
)

fun PartnerBudgetCoFinancing.toProjectPartnerBudgetCoFinancingDTO() = ProjectPartnerBudgetCoFinancingDTO(
    partner = partner.toDto(),
    projectPartnerCoFinancingAndContributionOutputDTO = projectPartnerCoFinancingAndContribution?.toDto(),
    total = total
)

fun Collection<PartnerBudgetCoFinancing>.toProjectPartnerBudgetDTO() = map { it.toProjectPartnerBudgetCoFinancingDTO() }
    .sortedBy { it.partner.sortNumber }

fun Collection<ProjectPartnerCoFinancing>.toCoFinancingDto() = map { it.toDto() }
    .sortedBy { it.fundType }

fun ProjectPartnerContribution.toDto(partnerAbbreviation: String) = ProjectPartnerContributionDTO(
    id = id,
    name = if (isPartner) partnerAbbreviation else name,
    status = status,
    partner = isPartner,
    amount = amount
)

fun Collection<ProjectPartnerContribution>.toContributionDto(partnerAbbreviation: String): List<ProjectPartnerContributionDTO> =
    if (isEmpty())
        listOf(ProjectPartnerContributionDTO(partner = true, name = partnerAbbreviation))
    else
        map { it.toDto(partnerAbbreviation) }.sortedWith(compareBy({ !it.partner }, { it.id }))
