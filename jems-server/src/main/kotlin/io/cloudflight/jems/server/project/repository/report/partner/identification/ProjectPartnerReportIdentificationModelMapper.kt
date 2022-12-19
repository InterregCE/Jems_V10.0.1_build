package io.cloudflight.jems.server.project.repository.report.partner.identification

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportDesignatedControllerEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportOnTheSpotVerificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationGeneralMethodologyEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationOnTheSpotLocationEntity
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.*
import java.math.BigDecimal

fun ProjectPartnerReportIdentificationEntity.toModel(
    targetGroups: List<ProjectPartnerReportIdentificationTargetGroupEntity>,
    periodResolver: (Int?) -> ProjectPartnerReportBudgetPerPeriodEntity?
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
    type = type
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

fun ProjectPartnerReportDesignatedControllerEntity.toModel() = ReportDesignatedController(
    controlInstitution = if (reportEntity.status.isCertified()) institutionName else controlInstitution.name,
    controlInstitutionId = controlInstitution.id,
    controllingUserId = controllingUser?.id,
    jobTitle = jobTitle,
    divisionUnit = divisionUnit,
    address = address,
    countryCode = countryCode,
    country = country,
    telephone = telephone,
    controllerReviewerId = controllerReviewer?.id
)

fun ProjectPartnerReportVerificationEntity.toModel() = ReportVerification(
    generalMethodologies = generalMethodologies.map { it.toModel() }.toSet(),
    verificationInstances = verificationInstances.map {it.toModel()}.toSet(),
    riskBasedVerificationApplied = riskBasedVerificationApplied,
    riskBasedVerificationDescription = riskBasedVerificationDescription
)

fun ProjectPartnerReportVerificationGeneralMethodologyEntity.toModel() = this.methodology

fun ProjectPartnerReportOnTheSpotVerificationEntity.toModel() = ReportOnTheSpotVerification(
    id = id,
    verificationFrom = verificationFrom,
    verificationTo = verificationTo,
    verificationFocus = verificationFocus,
    verificationLocations = verificationLocations.map { it.toModel() }.toSet()
)

fun ProjectPartnerReportVerificationOnTheSpotLocationEntity.toModel() = this.location

fun ReportVerification.toEntity(reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportVerificationEntity(
    reportEntity = reportEntity,
    generalMethodologies = generalMethodologies.map { it.toEntity(reportEntity.id) }.toMutableSet(),
    riskBasedVerificationApplied = riskBasedVerificationApplied,
    riskBasedVerificationDescription = riskBasedVerificationDescription
)

fun ReportMethodology.toEntity(reportVerificationId: Long) = ProjectPartnerReportVerificationGeneralMethodologyEntity(
    reportVerificationId = reportVerificationId,
    methodology = this
)

fun ReportOnTheSpotVerification.toEntity(reportVerificationId: Long) = ProjectPartnerReportOnTheSpotVerificationEntity(
    // verificationLocations needs ProjectPartnerReportOnTheSpotVerificationEntity id
    reportVerificationId = reportVerificationId,
    verificationFrom = verificationFrom,
    verificationTo = verificationTo,
    verificationFocus = verificationFocus,
    verificationLocations = if (id == 0L) mutableSetOf() else verificationLocations.map { it.toEntity(id) }.toMutableSet()
)

fun ReportLocationOnTheSpotVerification.toEntity(reportOnTheSpotVerificationId: Long) = ProjectPartnerReportVerificationOnTheSpotLocationEntity(
    reportOnTheSpotVerificationId = reportOnTheSpotVerificationId,
    location = this
)
