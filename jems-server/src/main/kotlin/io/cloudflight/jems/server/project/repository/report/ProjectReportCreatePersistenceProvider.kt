package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.contribution.toEntity
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportLumpSumRepository
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
import io.cloudflight.jems.server.project.service.report.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportCreatePersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val legalStatusRepository: ProgrammeLegalStatusRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val programmeLumpSumRepository: ProgrammeLumpSumRepository,
    private val workPlanRepository: ProjectPartnerReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
    private val identificationRepository: ProjectPartnerReportIdentificationRepository,
    private val identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository,
    private val contributionRepository: ProjectPartnerReportContributionRepository,
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
) : ProjectReportCreatePersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report.identification.coFinancing, report = reportEntity)
        persistWorkPlanToReport(report.workPackages, report = reportEntity)
        persistTargetGroupsToReport(report.targetGroups, report = reportEntity)
        persistContributionsToReport(report.budget.contributions, report = reportEntity)
        persistAvailableLumpSumsToReport(report.budget.lumpSums, report = reportEntity)
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

    private fun persistContributionsToReport(
        contributions: List<CreateProjectPartnerReportContribution>,
        report: ProjectPartnerReportEntity,
    ) =
        contributionRepository.saveAll(
            contributions.map { it.toEntity(report, attachment = null) }
        )

    private fun persistAvailableLumpSumsToReport(
        lumpSums: List<PartnerReportLumpSum>,
        report: ProjectPartnerReportEntity,
    ) =
        reportLumpSumRepository.saveAll(
            lumpSums.map { ls -> ls.toEntity(report, lumpSumResolver = { programmeLumpSumRepository.getById(it) }) }
        )

}
