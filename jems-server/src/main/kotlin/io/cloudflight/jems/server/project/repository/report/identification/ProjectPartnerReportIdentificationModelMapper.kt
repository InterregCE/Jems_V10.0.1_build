package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportFileFormat
import java.math.BigDecimal

fun ProjectPartnerReportIdentificationEntity.toModel(
    targetGroups: List<ProjectPartnerReportIdentificationTargetGroupEntity>,
    periodResolver: (Int?) -> ProjectPartnerReportBudgetPerPeriodEntity?,
) = ProjectPartnerReportIdentification(
    startDate = startDate,
    endDate = endDate,
    summary = translatedValues.extractField { it.summary },
    problemsAndDeviations = translatedValues.extractField { it.problemsAndDeviations },
    spendingDeviations = translatedValues.extractField { it.spendingDeviations },
    targetGroups = targetGroups.toModel(),
    spendingProfile = ProjectPartnerReportSpendingProfile(
        periodDetail = periodResolver.invoke(periodNumber)?.toModel(),
        currentReport = BigDecimal.ZERO /* temporarily, is calculated in service */,
        previouslyReported = BigDecimal.ZERO /* temporarily, is calculated in service */,
        differenceFromPlan = BigDecimal.ZERO,
        differenceFromPlanPercentage = BigDecimal.ZERO,
        nextReportForecast = nextReportForecast,
    ),
    controllerFormats = mapOf(
        ReportFileFormat.Originals to formatOriginals,
        ReportFileFormat.Copy to formatCopy,
        ReportFileFormat.Electronic to formatElectronic,
    ).filter { it.value }.keys,
    type = type,
)

fun ProjectPartnerReportBudgetPerPeriodEntity.toModel() = ProjectPartnerReportPeriod(
    number = id.periodNumber,
    periodBudget = periodBudget,
    periodBudgetCumulative = periodBudgetCumulative,
    start = startMonth,
    end = endMonth,
)

fun List<ProjectPartnerReportIdentificationTargetGroupEntity>.toModel() = map {
    ProjectPartnerReportIdentificationTargetGroup(
        type = it.type,
        sortNumber = it.sortNumber,
        specification = it.translatedValues.extractField { it.specification },
        description = it.translatedValues.extractField { it.description },
    )
}

fun List<ProjectPartnerReportBudgetPerPeriodEntity>.toPeriodModel() = map { it.toModel() }
