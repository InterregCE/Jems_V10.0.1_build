package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportUnitCostBase
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import java.math.BigDecimal

fun List<ProjectPartnerReportContributionEntity>.toModel() = map { mapper.map(it) }

fun CreateProjectPartnerReportContribution.toEntity(
    report: ProjectPartnerReportEntity,
    attachment: ReportProjectFileEntity?,
) = ProjectPartnerReportContributionEntity(
    reportEntity = report,
    sourceOfContribution = sourceOfContribution,
    legalStatus = legalStatus,
    idFromApplicationForm = idFromApplicationForm,
    historyIdentifier = historyIdentifier,
    createdInThisReport = createdInThisReport,
    amount = amount,
    previouslyReported = previouslyReported,
    currentlyReported = currentlyReported,
    attachment = attachment,
)

fun PartnerReportLumpSum.toEntity(
    report: ProjectPartnerReportEntity,
    lumpSumResolver: (Long) -> ProgrammeLumpSumEntity,
) = PartnerReportLumpSumEntity(
    reportEntity = report,
    programmeLumpSum = lumpSumResolver.invoke(lumpSumId),
    orderNr = orderNr,
    period = period,
    total = total,
    current = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
)

fun PartnerReportUnitCostBase.toEntity(
    report: ProjectPartnerReportEntity,
    unitCostResolver: (Long) -> ProgrammeUnitCostEntity,
) = PartnerReportUnitCostEntity(
    reportEntity = report,
    programmeUnitCost = unitCostResolver.invoke(unitCostId),
    numberOfUnits = numberOfUnits,
    total = totalCost,
    current = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
)

private val mapper = Mappers.getMapper(ProjectPartnerReportContributionModelMapper::class.java)

@Mapper
interface ProjectPartnerReportContributionModelMapper {
    fun map(entity: ProjectPartnerReportContributionEntity): ProjectPartnerReportEntityContribution
}
