package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.PartnerReportExpenditureCostsRepository
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.toEntity
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.updateEntity
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.updateTranslations
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.toEntity
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val legalStatusRepository: ProgrammeLegalStatusRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val workPlanRepository: ProjectPartnerReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
    private val identificationRepository: ProjectPartnerReportIdentificationRepository,
    private val identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository,
    private val partnerReportExpenditureCostsRepository: PartnerReportExpenditureCostsRepository
) : ProjectReportPersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report.identification.coFinancing, report = reportEntity)
        persistWorkPlanToReport(report.workPackages, report = reportEntity)
        persistTargetGroupsToReport(report.targetGroups, report = reportEntity)
        return reportEntity.toModelSummary()
    }

    private fun persistReport(report: ProjectPartnerReportCreate): ProjectPartnerReportEntity =
        partnerReportRepository.save(
            report.toEntity(
                legalStatus = report.identification.legalStatusId?.let { legalStatusRepository.getById(it) }
            )
        )

    private fun persistCoFinancingToReport(
        coFinancing: List<ProjectPartnerCoFinancing>,
        report: ProjectPartnerReportEntity,
    ) {
        partnerReportCoFinancingRepository.saveAll(
            coFinancing.toEntity(
                reportEntity = report,
                programmeFundResolver = { programmeFundRepository.getById(it) },
            )
        )
    }

    private fun persistWorkPlanToReport(
        workPackages: List<CreateProjectPartnerReportWorkPackage>,
        report: ProjectPartnerReportEntity,
    ) {
        workPackages.forEach { wp ->
            // save WP
            val wpEntity = workPlanRepository.save(wp.toEntity(report))
            // save WP activities
            wp.activities.forEach { activity ->
                val activityEntity = workPlanActivityRepository.save(activity.toEntity(wpEntity))
                // save nested WP activity deliverables
                workPlanActivityDeliverableRepository.saveAll(activity.deliverables.toEntity(activityEntity))
            }
            // save WP outputs
            workPlanOutputRepository.saveAll(wp.outputs.toEntity(wpEntity))
        }
    }

    private fun persistTargetGroupsToReport(
        targetGroups: List<ProjectRelevanceBenefit>,
        report: ProjectPartnerReportEntity,
    ) {
        val identification = identificationRepository.save(
            ProjectPartnerReportIdentificationEntity(
                reportEntity = report,
                startDate = null,
                endDate = null,
                periodNumber = null,
                translatedValues = mutableSetOf(),
            )
        )
        identificationTargetGroupRepository.saveAll(
            targetGroups.mapIndexed { index, benefit ->
                ProjectPartnerReportIdentificationTargetGroupEntity(
                    reportIdentificationEntity = identification,
                    type = ProjectTargetGroup.valueOf(benefit.group.name),
                    sortNumber = index.plus(1),
                    translatedValues = mutableSetOf(),
                ).apply {
                    translatedValues.addAll(
                        benefit.specification.map {
                            ProjectPartnerReportIdentificationTargetGroupTranslEntity(
                                translationId = TranslationId(this, it.language),
                                specification = it.translation,
                                description = null,
                            )
                        }
                    )
                }
            }
        )
    }

    @Transactional
    override fun submitReportById(
        partnerId: Long,
        reportId: Long,
        submissionTime: ZonedDateTime
    ): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                status = ReportStatus.Submitted
                firstSubmission = submissionTime
            }.toSubmissionSummary()

    @Transactional(readOnly = true)
    override fun getPartnerReportStatusAndVersion(
        partnerId: Long,
        reportId: Long
    ): ProjectPartnerReportStatusAndVersion =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).let {
            ProjectPartnerReportStatusAndVersion(it.status, it.applicationFormVersion)
        }

    @Transactional(readOnly = true)
    override fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toModel(
            coFinancing = partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
        )

    @Transactional(readOnly = true)
    override fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerId(partnerId = partnerId, pageable = pageable)
            .map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int =
        partnerReportRepository.getMaxNumberForPartner(partnerId = partnerId)

    @Transactional
    override fun updatePartnerReportExpenditureCosts(
        partnerReportId: Long,
        expenditureCosts: List<PartnerReportExpenditureCost>
    ): ProjectPartnerReportEntity {
        val reportPartner = getReportPartnerOrThrow(partnerReportId).also {
            updateExpenditureCosts(it, expenditureCosts.toSet())
        }
        return reportPartner
    }

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCosts(partnerReportId: Long): List<PartnerReportExpenditureCostEntity> {
        return partnerReportExpenditureCostsRepository.findAllByPartnerReportIdOrderById(partnerReportId)
    }

    private fun getReportPartnerOrThrow(partnerReportId: Long): ProjectPartnerReportEntity {
        return partnerReportRepository.findById(partnerReportId)
            .orElseThrow { ResourceNotFoundException("partnerReport") }
    }

    private fun updateExpenditureCosts(
        partnerReport: ProjectPartnerReportEntity, expenditureCosts: Set<PartnerReportExpenditureCost>
    ) {
        val newExpenditureCosts = expenditureCosts.map {
            if (it.id == null) {
                it.toEntity(partnerReport)
            } else {
                partnerReport.expenditureCosts.first { expenditureCost -> expenditureCost.id == it.id }
                    .apply {
                        this.updateEntity(it)
                        this.updateTranslations(it.comment, it.description)
                    }
            }
        }
        partnerReport.expenditureCosts.clear()
        partnerReport.expenditureCosts.addAll(newExpenditureCosts)
    }

    @Transactional(readOnly = true)
    override fun countForPartner(partnerId: Long): Int =
        partnerReportRepository.countAllByPartnerId(partnerId)

}
