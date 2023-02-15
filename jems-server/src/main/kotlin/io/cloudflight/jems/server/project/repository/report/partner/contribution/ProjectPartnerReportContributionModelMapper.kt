package io.cloudflight.jems.server.project.repository.report.partner.contribution

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import java.math.BigDecimal

fun List<ProjectPartnerReportContributionEntity>.toModel() = map { mapper.map(it) }

fun CreateProjectPartnerReportContribution.toEntity(
    report: ProjectPartnerReportEntity,
    attachment: JemsFileMetadataEntity?,
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
    totalEligibleAfterControl = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
    previouslyPaid = previouslyPaid,
    currentParked = BigDecimal.ZERO,
    currentReIncluded = BigDecimal.ZERO,
    previouslyReportedParked = previouslyReportedParked,
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
    totalEligibleAfterControl = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
    currentParked = BigDecimal.ZERO,
    currentReIncluded = BigDecimal.ZERO,
    previouslyReportedParked = previouslyReportedParked,
)

fun PartnerReportInvestment.toEntity(
    report: ProjectPartnerReportEntity,
) = PartnerReportInvestmentEntity(
    reportEntity = report,
    investmentId = investmentId,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber,
    translatedValues = mutableSetOf(),
    deactivated = deactivated,
    total = total,
    current = BigDecimal.ZERO,
    totalEligibleAfterControl = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
    currentParked = BigDecimal.ZERO,
    currentReIncluded = BigDecimal.ZERO,
    previouslyReportedParked = previouslyReportedParked
).apply {
    translatedValues.addAll(
        combineInvestmentTranslatedValues(this, title)
    )
}

fun combineInvestmentTranslatedValues(
    sourceEntity: PartnerReportInvestmentEntity,
    title: Set<InputTranslation>,
): MutableSet<PartnerReportInvestmentTranslEntity> {
    val titleMap = title.filter { !it.translation.isNullOrBlank() }
        .associateBy( { it.language }, { it.translation } )

    return titleMap.keys.mapTo(HashSet()) {
        PartnerReportInvestmentTranslEntity(
            TranslationId(sourceEntity, it),
            title = titleMap[it]!!,
        )
    }
}

private val mapper = Mappers.getMapper(ProjectPartnerReportContributionModelMapper::class.java)

@Mapper
interface ProjectPartnerReportContributionModelMapper {
    fun map(entity: ProjectPartnerReportContributionEntity): ProjectPartnerReportEntityContribution
}
