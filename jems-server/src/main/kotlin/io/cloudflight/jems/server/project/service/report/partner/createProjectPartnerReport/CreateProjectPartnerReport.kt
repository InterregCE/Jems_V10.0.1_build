package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectPartnerReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val reportPersistence: ProjectReportPersistence,
) : CreateProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerReportException::class)
    override fun createReportFor(partnerId: Long): ProjectPartnerReportSummary {
        val project = projectPersistence.getProjectSummary(
            projectId = projectPartnerPersistence.getProjectIdForPartnerId(partnerId)
        )

        validateProjectIsContracted(project)

        return reportPersistence.createPartnerReport(
            generateReport(
                project = project,
                partnerId = partnerId,
                version = versionPersistence.getLatestApprovedOrCurrent(projectId = project.id),
            )
        )
    }


    private fun validateProjectIsContracted(project: ProjectSummary) {
        if (!project.status.isAlreadyContracted())
            throw ReportCanBeCreatedOnlyWhenContractedException()
    }

    private fun generateReport(project: ProjectSummary, partnerId: Long, version: String) = ProjectPartnerReportCreate(
        partnerId = partnerId,
        reportNumber = getLatestReportNumberIncreasedByOne(partnerId),
        status = ReportStatus.Draft,
        version = version,

        identification = projectPartnerPersistence.getById(partnerId, version).let {
            it.toReportIdentification(project).apply {
                coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version).finances
            }
        }
    )

    private fun getLatestReportNumberIncreasedByOne(partnerId: Long) =
        reportPersistence.getCurrentLatestReportNumberForPartner(partnerId).plus(1)

    private fun ProjectPartnerDetail.toReportIdentification(project: ProjectSummary) = PartnerReportIdentificationCreate(
        projectIdentifier = project.customIdentifier,
        projectAcronym = project.acronym,
        partnerNumber = sortNumber!!,
        partnerAbbreviation = abbreviation,
        partnerRole = role,
        nameInOriginalLanguage = nameInOriginalLanguage,
        nameInEnglish = nameInEnglish,
        legalStatusId = legalStatusId,
        partnerType = partnerType,
        vatRecovery = vatRecovery,
        coFinancing = emptyList(),
    )

}
