package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileId
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ReportProjectCertificateCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory.ReportProjectCertificateCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.lumpSums.ReportProjectCertificateLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.unitCosts.ReportProjectCertificateUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportHorizontalPrincipleRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.toIndexedEntity
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageRepository
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportPartnerCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportResultCreate
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportCreatePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCreatePersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val contractingDeadlineRepository: ProjectContractingReportingRepository,
    private val reportIdentificationTargetGroupRepository: ProjectReportIdentificationTargetGroupRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository,
    private val projectReportCertificateCoFinancingRepository: ReportProjectCertificateCoFinancingRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val workPlanRepository: ProjectReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectReportWorkPackageOutputRepository,
    private val projectReportCoFinancingRepository: ProjectReportCoFinancingRepository,
    private val projectReportCertificateCostCategoryRepository: ReportProjectCertificateCostCategoryRepository,
    private val resultIndicatorRepository: ResultIndicatorRepository,
    private val outputIndicatorRepository: OutputIndicatorRepository,
    private val projectResultRepository: ProjectReportProjectResultRepository,
    private val horizontalPrincipleRepository: ProjectReportHorizontalPrincipleRepository,
    private val reportProjectCertificateLumpSumRepository: ReportProjectCertificateLumpSumRepository,
    private val programmeLumpSumRepository: ProgrammeLumpSumRepository,
    private val programmeUnitCostRepository: ProgrammeUnitCostRepository,
    private val reportProjectCertificateUnitCostRepository: ReportProjectCertificateUnitCostRepository,
) : ProjectReportCreatePersistence {

    @Transactional
    override fun createReportAndFillItToEmptyCertificates(reportToCreate: ProjectReportCreateModel): ProjectReportModel {
        val reportPersisted = persistBaseIdentification(reportToCreate.reportBase)

        persistWorkPlan(reportToCreate.workPackages, reportPersisted)
        persistTargetGroups(reportToCreate.targetGroups, reportPersisted)
        persistPartners(reportToCreate.partners, reportPersisted)
        persistCoFinancing(reportToCreate.reportBudget.coFinancing, reportPersisted)
        persistCostCategories(reportToCreate.reportBudget.costCategorySetup, reportPersisted)
        persistAvailableLumpSums(reportToCreate.reportBudget.availableLumpSums, reportPersisted)
        persistUnitCosts(reportToCreate.reportBudget.unitCosts, reportPersisted)
        persistResultsAndHorizontalPrinciples(reportToCreate.results, reportToCreate.horizontalPrinciples, reportPersisted)

        fillProjectReportToAllEmptyCertificates(projectId = reportToCreate.reportBase.projectId, reportPersisted)

        return reportPersisted.toModel()
    }

    private fun persistBaseIdentification(report: ProjectReportModel): ProjectReportEntity =
        projectReportRepository.save(
            report.toEntity(deadlineResolver = { contractingDeadlineRepository.findByProjectIdAndId(report.projectId, it) })
        )

    private fun persistWorkPlan(workPackages: List<ProjectReportWorkPackageCreate>, report: ProjectReportEntity) {
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
            workPlanOutputRepository.saveAll(wp.outputs.toEntity(wpEntity, { outputIndicatorRepository.getById(it) }))
        }
    }

    private fun persistTargetGroups(targetGroups: List<ProjectRelevanceBenefit>, reportEntity: ProjectReportEntity) {
        reportIdentificationTargetGroupRepository.saveAll(
            targetGroups.mapIndexed { index, targetGroup ->
                ProjectReportIdentificationTargetGroupEntity(
                    projectReportEntity = reportEntity,
                    type = ProjectTargetGroup.valueOf(targetGroup.group.name),
                    sortNumber = index.plus(1),
                )
            }
        )
    }

    private fun fillProjectReportToAllEmptyCertificates(projectId: Long, report: ProjectReportEntity) {
        val partnerIds = partnerRepository.findTop30ByProjectId(projectId).mapTo(HashSet()) { it.id }

        partnerReportRepository.findAllByPartnerIdInAndProjectReportNullAndStatus(partnerIds, ReportStatus.Certified).forEach {
            it.projectReport = report
        }
    }

    private fun persistPartners(
        partners: List<ProjectReportPartnerCreateModel>,
        reportPersisted: ProjectReportEntity,
    ) {
        val spendingProfiles = partners.map {
            ProjectReportSpendingProfileEntity(
                id = ProjectReportSpendingProfileId(reportPersisted, it.partnerId),
                partnerNumber = it.partnerNumber,
                partnerAbbreviation = it.partnerAbbreviation,
                partnerRole = it.partnerRole,
                country = it.country,
                previouslyReported = it.previouslyReported,
                currentlyReported = BigDecimal.ZERO,
            )
        }
        projectReportSpendingProfileRepository.saveAll(spendingProfiles)
    }

    private fun persistCoFinancing(
        coFinancing: PreviouslyProjectReportedCoFinancing,
        report: ProjectReportEntity,
    ) {
        projectReportCoFinancingRepository.saveAll(
            coFinancing.fundsSorted.toProjectReportEntity(
                reportEntity = report,
                programmeFundResolver = { programmeFundRepository.getById(it) },
            )
        )

        projectReportCertificateCoFinancingRepository.save(
            coFinancing.toProjectReportEntity(report),
        )
    }

    private fun persistCostCategories(
        certificateCostCategory: ReportCertificateCostCategory,
        report: ProjectReportEntity,
    ) =
        projectReportCertificateCostCategoryRepository.save(certificateCostCategory.toCreateEntity(report = report))

    private fun persistResultsAndHorizontalPrinciples(
        projectResults: List<ProjectReportResultCreate>,
        horizontalPrinciples: ProjectHorizontalPrinciples,
        projectReport: ProjectReportEntity
    ) {
        projectResultRepository.saveAll(
            projectResults.toIndexedEntity(
                projectReport = projectReport,
                indicatorEntityResolver = { it?.let { resultIndicatorRepository.getById(it) } },
            )
        )

        horizontalPrincipleRepository.save(
            ProjectReportHorizontalPrincipleEntity(
                projectReport = projectReport,
                sustainableDevelopmentCriteriaEffect = horizontalPrinciples.sustainableDevelopmentCriteriaEffect,
                equalOpportunitiesEffect = horizontalPrinciples.equalOpportunitiesEffect,
                sexualEqualityEffect = horizontalPrinciples.sexualEqualityEffect
            )
        )
    }

    private fun persistAvailableLumpSums(
        lumpSums: List<ProjectReportLumpSum>,
        report: ProjectReportEntity,
    ) =
        reportProjectCertificateLumpSumRepository.saveAll(
            lumpSums.map { ls -> ls.toEntity(report, lumpSumResolver = { programmeLumpSumRepository.getById(it) }) }
        )

    private fun persistUnitCosts(
        unitCosts: Set<ProjectReportUnitCostBase>,
        report: ProjectReportEntity,
    ) =
        reportProjectCertificateUnitCostRepository.saveAll(
            unitCosts.map { ls -> ls.toEntity(report, unitCostResolver = { programmeUnitCostRepository.getById(it) }) }
        )

}
