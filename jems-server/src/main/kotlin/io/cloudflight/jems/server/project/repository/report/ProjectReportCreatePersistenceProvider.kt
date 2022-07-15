package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodId
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.contribution.toEntity
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing.ReportProjectPartnerExpenditureCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportBudgetPerPeriodRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.toEntity
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.server.project.service.report.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCreatePersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val reportProjectPartnerExpenditureCoFinancingRepository: ReportProjectPartnerExpenditureCoFinancingRepository,
    private val legalStatusRepository: ProgrammeLegalStatusRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val programmeLumpSumRepository: ProgrammeLumpSumRepository,
    private val programmeUnitCostRepository: ProgrammeUnitCostRepository,
    private val workPlanRepository: ProjectPartnerReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
    private val identificationRepository: ProjectPartnerReportIdentificationRepository,
    private val identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository,
    private val contributionRepository: ProjectPartnerReportContributionRepository,
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
    private val reportUnitCostRepository: ProjectPartnerReportUnitCostRepository,
    private val reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository,
    private val reportBudgetExpenditureRepository: ReportProjectPartnerExpenditureCostCategoryRepository,
) : ProjectReportCreatePersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report, report = reportEntity)
        persistWorkPlanToReport(report.workPackages, report = reportEntity)
        persistTargetGroupsAndSpendingToReport(report.targetGroups, report = reportEntity)
        persistContributionsToReport(report.budget.contributions, report = reportEntity)
        persistAvailableLumpSumsToReport(report.budget.lumpSums, report = reportEntity)
        persistAvailableUnitCostsToReport(report.budget.unitCosts, report = reportEntity)
        persistBudgetPerPeriodToReport(report.budget.budgetPerPeriod, report = reportEntity)
        persistBudgetExpenditureSetupToReport(report.budget.expenditureSetup, report = reportEntity)
        return reportEntity.toModelSummary()
    }

    private fun persistReport(report: ProjectPartnerReportCreate): ProjectPartnerReportEntity =
        partnerReportRepository.save(
            report.toEntity(
                legalStatus = report.identification.legalStatusId?.let { legalStatusRepository.getById(it) }
            )
        )

    private fun persistCoFinancingToReport(
        reportData: ProjectPartnerReportCreate,
        report: ProjectPartnerReportEntity,
    ) {
        partnerReportCoFinancingRepository.saveAll(
            reportData.budget.previouslyReportedCoFinancing.fundsSorted.toEntity(
                reportEntity = report,
                programmeFundResolver = { programmeFundRepository.getById(it) },
            )
        )

        reportProjectPartnerExpenditureCoFinancingRepository.save(
            reportData.budget.previouslyReportedCoFinancing.toEntity(report),
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

    private fun persistTargetGroupsAndSpendingToReport(
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
                nextReportForecast = BigDecimal.ZERO,
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

    private fun persistAvailableUnitCostsToReport(
        unitCosts: Set<PartnerReportUnitCostBase>,
        report: ProjectPartnerReportEntity,
    ) =
        reportUnitCostRepository.saveAll(
            unitCosts.map { uc -> uc.toEntity(report, unitCostResolver = { programmeUnitCostRepository.getById(it) }) }
        )

    private fun persistBudgetPerPeriodToReport(
        budgetPerPeriod: Collection<ProjectPartnerReportPeriod>,
        report: ProjectPartnerReportEntity,
    ) =
        reportBudgetPerPeriodRepository.saveAll(
            budgetPerPeriod.map {
                ProjectPartnerReportBudgetPerPeriodEntity(
                    id = ProjectPartnerReportBudgetPerPeriodId(
                        report = report,
                        periodNumber = it.number,
                    ),
                    periodBudget = it.periodBudget,
                    periodBudgetCumulative = it.periodBudgetCumulative,
                    startMonth = it.start,
                    endMonth = it.end,
                )
            }
        )

    private fun persistBudgetExpenditureSetupToReport(
        expenditureCostCategory: ReportExpenditureCostCategory,
        report: ProjectPartnerReportEntity,
    ) =
        reportBudgetExpenditureRepository.save(expenditureCostCategory.toCreateEntity(report = report))

}
