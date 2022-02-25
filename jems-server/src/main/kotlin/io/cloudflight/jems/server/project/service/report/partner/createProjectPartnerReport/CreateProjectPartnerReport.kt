package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partnerReportCreated
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectPartnerReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val projectWorkPackagePersistence: WorkPackagePersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : CreateProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerReportException::class)
    override fun createReportFor(partnerId: Long): ProjectPartnerReportSummary {
        val projectId = projectPartnerPersistence.getProjectIdForPartnerId(partnerId)
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)

        val project = projectPersistence.getProject(projectId = projectId, version = version)

        validateProjectIsContracted(project)

        val report = generateReport(project = project, partnerId = partnerId, version = version)

        return reportPersistence.createPartnerReport(report).also {
            auditPublisher.publishEvent(partnerReportCreated(this, project, report, it.id))
        }
    }


    private fun validateProjectIsContracted(project: ProjectFull) {
        if (!project.projectStatus.status.isAlreadyContracted())
            throw ReportCanBeCreatedOnlyWhenContractedException()
    }

    private fun generateReport(project: ProjectFull, partnerId: Long, version: String) = ProjectPartnerReportCreate(
        partnerId = partnerId,
        reportNumber = getLatestReportNumberIncreasedByOne(partnerId),
        status = ReportStatus.Draft,
        version = version,

        identification = projectPartnerPersistence.getById(partnerId, version).let {
            it.toReportIdentification(project).apply {
                coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version).finances
            }
        },

        workPackages = projectWorkPackagePersistence
            .getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId = project.id!!, version = version)
            .toCreateEntity()
    )

    private fun getLatestReportNumberIncreasedByOne(partnerId: Long) =
        reportPersistence.getCurrentLatestReportNumberForPartner(partnerId).plus(1)

    private fun ProjectPartnerDetail.toReportIdentification(project: ProjectFull) = PartnerReportIdentificationCreate(
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

    private fun List<ProjectWorkPackage>.toCreateEntity() = map { wp ->
        CreateProjectPartnerReportWorkPackage(
            workPackageId = wp.id,
            number = wp.workPackageNumber,
            activities = wp.activities.map { a ->
                CreateProjectPartnerReportWorkPackageActivity(
                    activityId = a.id,
                    number = a.activityNumber,
                    title = a.title,
                    deliverables = a.deliverables.map { d ->
                        CreateProjectPartnerReportWorkPackageActivityDeliverable(
                            deliverableId = d.id,
                            number = d.deliverableNumber,
                            title = d.title,
                        )
                    },
                )
            },
             outputs = wp.outputs.map { o ->
                 CreateProjectPartnerReportWorkPackageOutput(
                     number = o.outputNumber,
                     title = o.title,
                 )
             },
        )
    }

}
