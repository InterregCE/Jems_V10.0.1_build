package io.cloudflight.jems.server.project.controller.report.contribution

import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.update.UpdateProjectPartnerReportContributionWrapper
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportContributionMapper::class.java)

fun ProjectPartnerReportContribution.toDto() =
    mapper.map(this)

fun ProjectPartnerReportContributionOverview.toDto() =
    mapper.map(this)

fun UpdateProjectPartnerReportContributionDataDTO.toModel() =
    mapper.map(this)

@Mapper
interface ProjectPartnerReportContributionMapper {
    fun map(model: ProjectPartnerReportContribution): ProjectPartnerReportContributionDTO
    @Mappings(
        Mapping(source = "public", target = "publicContribution"),
        Mapping(source = "automaticPublic", target = "automaticPublicContribution"),
        Mapping(source = "private", target = "privateContribution"),
    )
    fun map(model: ProjectPartnerReportContributionOverview): ProjectPartnerReportContributionOverviewDTO
    fun map(dto: UpdateProjectPartnerReportContributionDataDTO): UpdateProjectPartnerReportContributionWrapper
}
