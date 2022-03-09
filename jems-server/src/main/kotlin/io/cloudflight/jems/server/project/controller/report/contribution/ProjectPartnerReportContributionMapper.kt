package io.cloudflight.jems.server.project.controller.report.contribution

import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionWrapper
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportIdentificationMapper::class.java)

fun ProjectPartnerReportContribution.toDto() =
    mapper.map(this)

fun ProjectPartnerReportContributionOverview.toDto() =
    mapper.map(this)

fun UpdateProjectPartnerReportContributionDataDTO.toModel() =
    mapper.map(this)

@Mapper
interface ProjectPartnerReportIdentificationMapper {
    fun map(model: ProjectPartnerReportContribution): ProjectPartnerReportContributionDTO
    fun map(model: ProjectPartnerReportContributionOverview): ProjectPartnerReportContributionOverviewDTO
    fun map(dto: UpdateProjectPartnerReportContributionDataDTO): UpdateProjectPartnerReportContributionWrapper
}
