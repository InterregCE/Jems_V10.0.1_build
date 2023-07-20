package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateUnitCostEntity
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import java.math.BigDecimal


fun ProjectReportEntity.toModelSummary(
    periodResolver: (Int) -> ProjectPeriod?,
) = ProjectReportSummary(
    id = id,
    reportNumber = number,
    status = status,
    linkedFormVersion = applicationFormVersion,
    startDate = startDate,
    endDate = endDate,

    type = deadline?.type ?: type,
    periodDetail = (deadline?.periodNumber ?: periodNumber)?.let { periodResolver.invoke(it) },
    reportingDate = deadline?.deadline ?: reportingDate,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
    deletable = false,
    verificationEndDate = verificationEndDate,
    totalEligibleAfterVerification = null,
    amountRequested = null,

    verificationConclusionJS = verificationConclusionJs,
    verificationConclusionMA = verificationConclusionMa,
    verificationFollowup = verificationFollowup,

)

fun ProjectReportEntity.toModel() = ProjectReportModel(
    id = id,
    reportNumber = number,
    status = status,
    linkedFormVersion = applicationFormVersion,
    startDate = startDate,
    endDate = endDate,

    deadlineId = deadline?.id,
    type = deadline?.type ?: type,
    periodNumber = deadline?.periodNumber ?: periodNumber,
    reportingDate = deadline?.deadline ?: reportingDate,

    projectId = projectId,
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    leadPartnerNameInOriginalLanguage = leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = leadPartnerNameInEnglish,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
    verificationEndDate = verificationEndDate,
    amountRequested = null,
    totalEligibleAfterVerification = null
)

fun Pair<ProjectReportEntity, ReportProjectCertificateCoFinancingEntity?>.toModel() = ProjectReportModel(
    id = first.id,
    reportNumber = first.number,
    status = first.status,
    linkedFormVersion = first.applicationFormVersion,
    startDate = first.startDate,
    endDate = first.endDate,

    deadlineId = first.deadline?.id,
    type = first.deadline?.type ?: first.type,
    periodNumber = first.deadline?.periodNumber ?: first.periodNumber,
    reportingDate = first.deadline?.deadline ?: first.reportingDate,

    projectId = first.projectId,
    projectIdentifier = first.projectIdentifier,
    projectAcronym = first.projectAcronym,
    leadPartnerNameInOriginalLanguage = first.leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = first.leadPartnerNameInEnglish,

    createdAt = first.createdAt,
    firstSubmission = first.firstSubmission,
    verificationDate = first.verificationDate,
    verificationEndDate = first.verificationEndDate,
    amountRequested = second?.sumCurrent,
    totalEligibleAfterVerification = null,
)

fun ProjectReportModel.toEntity(
    deadlineResolver: (Long) -> ProjectContractingReportingEntity,
) = ProjectReportEntity(
    projectId = projectId,
    number = reportNumber,
    status = status,
    applicationFormVersion = linkedFormVersion,
    startDate = startDate,
    endDate = endDate,

    deadline = deadlineId?.let { deadlineResolver.invoke(it) },
    type = type,
    reportingDate = reportingDate,
    periodNumber = periodNumber,

    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    leadPartnerNameInOriginalLanguage = leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = leadPartnerNameInEnglish,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
    verificationEndDate = null,

    verificationConclusionJs = null,
    verificationConclusionMa = null,
    verificationFollowup = null,
)

fun ProjectReportEntity.toSubmissionSummary() =
    ProjectReportSubmissionSummary(
        id = id,
        reportNumber = number,
        status = status,
        version = applicationFormVersion,
        firstSubmission = firstSubmission,
        createdAt = createdAt,
        projectId = projectId,
        projectIdentifier = projectIdentifier,
        projectAcronym = projectAcronym,
    )

fun PreviouslyProjectReportedCoFinancing.toProjectReportEntity(
    reportEntity: ProjectReportEntity,
): ReportProjectCertificateCoFinancingEntity {
    return ReportProjectCertificateCoFinancingEntity(
        reportEntity = reportEntity,

        partnerContributionTotal = totalPartner,
        publicContributionTotal = totalPublic,
        automaticPublicContributionTotal = totalAutoPublic,
        privateContributionTotal = totalPrivate,
        sumTotal = totalSum,

        partnerContributionCurrent = BigDecimal.ZERO,
        publicContributionCurrent = BigDecimal.ZERO,
        automaticPublicContributionCurrent = BigDecimal.ZERO,
        privateContributionCurrent = BigDecimal.ZERO,
        sumCurrent = BigDecimal.ZERO,

        partnerContributionPreviouslyReported = previouslyReportedPartner,
        publicContributionPreviouslyReported = previouslyReportedPublic,
        automaticPublicContributionPreviouslyReported = previouslyReportedAutoPublic,
        privateContributionPreviouslyReported = previouslyReportedPrivate,
        sumPreviouslyReported = previouslyReportedSum,
    )
}


fun List<PreviouslyProjectReportedFund>.toProjectReportEntity(
    reportEntity: ProjectReportEntity,
    programmeFundResolver: (Long) -> ProgrammeFundEntity,
): List<ProjectReportCoFinancingEntity> {
    return mapIndexed { index, fund ->
        ProjectReportCoFinancingEntity(
            id = ProjectReportCoFinancingIdEntity(reportEntity, index.plus(1)),
            programmeFund = fund.fundId?.let { programmeFundResolver.invoke(it) },
            percentage = fund.percentage,
            total = fund.total,
            current = BigDecimal.ZERO,
            previouslyReported = fund.previouslyReported,
            previouslyPaid = fund.previouslyPaid,
        )
    }
}

fun ReportCertificateCostCategory.toCreateEntity(report: ProjectReportEntity) =
    ReportProjectCertificateCostCategoryEntity(
        reportEntity = report,

        staffTotal = totalsFromAF.staff,
        officeTotal = totalsFromAF.office,
        travelTotal = totalsFromAF.travel,
        externalTotal = totalsFromAF.external,
        equipmentTotal = totalsFromAF.equipment,
        infrastructureTotal = totalsFromAF.infrastructure,
        otherTotal = totalsFromAF.other,
        lumpSumTotal = totalsFromAF.lumpSum,
        unitCostTotal = totalsFromAF.unitCost,
        sumTotal = totalsFromAF.sum,

        staffCurrent = BigDecimal.ZERO,
        officeCurrent = BigDecimal.ZERO,
        travelCurrent = BigDecimal.ZERO,
        externalCurrent = BigDecimal.ZERO,
        equipmentCurrent = BigDecimal.ZERO,
        infrastructureCurrent = BigDecimal.ZERO,
        otherCurrent = BigDecimal.ZERO,
        lumpSumCurrent = BigDecimal.ZERO,
        unitCostCurrent = BigDecimal.ZERO,
        sumCurrent = BigDecimal.ZERO,

        staffPreviouslyReported = previouslyReported.staff,
        officePreviouslyReported = previouslyReported.office,
        travelPreviouslyReported = previouslyReported.travel,
        externalPreviouslyReported = previouslyReported.external,
        equipmentPreviouslyReported = previouslyReported.equipment,
        infrastructurePreviouslyReported = previouslyReported.infrastructure,
        otherPreviouslyReported = previouslyReported.other,
        lumpSumPreviouslyReported = previouslyReported.lumpSum,
        unitCostPreviouslyReported = previouslyReported.unitCost,
        sumPreviouslyReported = previouslyReported.sum,

        )

fun ProjectReportLumpSum.toEntity(
    report: ProjectReportEntity,
    lumpSumResolver: (Long) -> ProgrammeLumpSumEntity,
) = ReportProjectCertificateLumpSumEntity(
    reportEntity = report,
    programmeLumpSum = lumpSumResolver.invoke(lumpSumId),
    orderNr = orderNr,
    periodNumber = period,
    total = total,
    current = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
    previouslyPaid = previouslyPaid,
)

fun ProjectReportUnitCostBase.toEntity(
    report: ProjectReportEntity,
    unitCostResolver: (Long) -> ProgrammeUnitCostEntity,
) = ReportProjectCertificateUnitCostEntity(
    reportEntity = report,
    programmeUnitCost = unitCostResolver.invoke(unitCostId),
    total = totalCost,
    current = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
)


fun ProjectReportEntity.toVerificationConclusion() = ProjectReportVerificationConclusion(
    startDate = startDate,
    conclusionJS = verificationConclusionJs,
    conclusionMA = verificationConclusionMa,
    verificationFollowUp = verificationFollowup
)