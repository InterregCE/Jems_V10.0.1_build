package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

fun List<ProjectPartnerReportContributionEntity>.toModel() = map { mapper.map(it) }

fun CreateProjectPartnerReportContribution.toEntity(report: ProjectPartnerReportEntity) = ProjectPartnerReportContributionEntity(
    reportEntity = report,
    sourceOfContribution = sourceOfContribution,
    legalStatus = legalStatus,
    idFromApplicationForm = idFromApplicationForm,
    historyIdentifier = historyIdentifier,
    createdInThisReport = createdInThisReport,
    amount = amount,
    previouslyReported = previouslyReported,
    currentlyReported = currentlyReported,
)

private val mapper = Mappers.getMapper(ProjectPartnerReportContributionModelMapper::class.java)

@Mapper
interface ProjectPartnerReportContributionModelMapper {
    fun map(entity: ProjectPartnerReportContributionEntity): ProjectPartnerReportEntityContribution
}
