package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodId
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.partner.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.partner.contribution.toEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportInvestmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportProjectPartnerExpenditureCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportBudgetPerPeriodRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.toEntity
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackage
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportCreatePersistenceProvider(
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
    private val reportInvestmentRepository: ProjectPartnerReportInvestmentRepository,
    private val reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository,
    private val reportBudgetExpenditureRepository: ReportProjectPartnerExpenditureCostCategoryRepository,
) : ProjectPartnerReportCreatePersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report, report = reportEntity)
        persistWorkPlanToReport(report.workPackages, report = reportEntity)
        persistTargetGroupsAndSpendingToReport(report.targetGroups, report = reportEntity)
        persistContributionsToReport(report.budget.contributions, report = reportEntity)
        persistAvailableLumpSumsToReport(report.budget.availableLumpSums, report = reportEntity)
        persistAvailableUnitCostsToReport(report.budget.unitCosts, report = reportEntity)
        persistAvailableInvestmentsToReport(report.budget.investments, report = reportEntity)
        persistBudgetPerPeriodToReport(report.budget.budgetPerPeriod, report = reportEntity)
        persistBudgetExpenditureSetupToReport(report.budget.expenditureSetup, report = reportEntity)
        return reportEntity.toModelSummaryAfterCreate()
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
                formatOriginals = false,
                formatCopy = false,
                formatElectronic = false,
                type = ReportType.PartnerReport,
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

    private fun persistAvailableInvestmentsToReport(
        investments: List<PartnerReportInvestment>,
        report: ProjectPartnerReportEntity,
    ) =
        reportInvestmentRepository.saveAll(
            investments.map { investment -> investment.toEntity(report = report) }
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
