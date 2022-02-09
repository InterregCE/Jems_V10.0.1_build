package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.repository.legalstatus.toModel
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary

fun ProjectPartnerReportEntity.toModelSummary() = ProjectPartnerReportSummary(
    id = id,
    reportNumber = number,
    status = status,
    version = applicationFormVersion,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
)

fun ProjectPartnerReportEntity.toModel(coFinancing: List<ProjectPartnerReportCoFinancingEntity>) = ProjectPartnerReport(
    id = id,
    reportNumber = number,
    status = status,
    version = applicationFormVersion,

    identification = PartnerReportIdentification(
        projectIdentifier = identification.projectIdentifier,
        projectAcronym = identification.projectAcronym,
        partnerNumber = identification.partnerNumber,
        partnerAbbreviation = identification.partnerAbbreviation,
        partnerRole = identification.partnerRole,
        nameInOriginalLanguage = identification.nameInOriginalLanguage,
        nameInEnglish = identification.nameInEnglish,
        legalStatus = identification.legalStatus?.toModel(),
        partnerType = identification.partnerType,
        vatRecovery = identification.vatRecovery,
        coFinancing = coFinancing.toModel(),
    )
)

fun List<ProjectPartnerReportCoFinancingEntity>.toModel() = map {
    ProjectPartnerCoFinancing(
        fundType = it.programmeFund?.let { MainFund } ?: PartnerContribution,
        fund = it.programmeFund?.toModel(),
        percentage = it.percentage,
    )
}

fun ProjectPartnerReportCreate.toEntity(legalStatus: ProgrammeLegalStatusEntity?) = ProjectPartnerReportEntity(
    partnerId = partnerId,
    number = reportNumber,
    status = status,
    applicationFormVersion = version,
    firstSubmission = null,

    identification = PartnerReportIdentificationEntity(
        projectIdentifier = identification.projectIdentifier,
        projectAcronym = identification.projectAcronym,
        partnerNumber = identification.partnerNumber,
        partnerAbbreviation = identification.partnerAbbreviation,
        partnerRole = identification.partnerRole,
        nameInOriginalLanguage = identification.nameInOriginalLanguage,
        nameInEnglish = identification.nameInEnglish,
        legalStatus = legalStatus,
        partnerType = identification.partnerType,
        vatRecovery = identification.vatRecovery,
    ),
)

fun List<ProjectPartnerCoFinancing>.toEntity(
    reportEntity: ProjectPartnerReportEntity,
    programmeFundResolver: (Long) -> ProgrammeFundEntity,
): List<ProjectPartnerReportCoFinancingEntity> {
    return this.mapIndexed { index, coFinancing ->
        ProjectPartnerReportCoFinancingEntity(
            id = ProjectPartnerReportCoFinancingIdEntity(
                report = reportEntity,
                fundSortNumber = index.plus(1),
            ),
            programmeFund = coFinancing.fund?.let { fund -> programmeFundResolver.invoke(fund.id) },
            percentage = coFinancing.percentage,
        )
    }
}
