package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProjectDataProviderImpl(
    private val projectPersistence: ProjectPersistence,
    private val programmeLumpSumPersistence: ProgrammeLumpSumPersistence,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val resultPersistence: ProjectResultPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val associatedOrganizationService: ProjectAssociatedOrganizationService,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val coFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val budgetCostsCalculator: BudgetCostsCalculatorService,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
) : ProjectDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getProjectDataForProjectId(projectId: Long): ProjectData {
        val project = projectPersistence.getProject(projectId)
        val sectionA = project.toDataModel()

        val partners = partnerPersistence.findAllByProjectId(projectId).map {
            val budgetOptions = budgetOptionsPersistence.getBudgetOptions(it.id)?.toDataModel()
            val coFinancing = coFinancingPersistence.getCoFinancingAndContributions(it.id).toDataModel()
            val budgetCosts = BudgetCosts(
                staffCosts = getBudgetCostsPersistence.getBudgetStaffCosts(it.id),
                travelCosts = getBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(it.id),
                externalCosts = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(it.id),
                equipmentCosts = getBudgetCostsPersistence.getBudgetEquipmentCosts(it.id),
                infrastructureCosts = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(it.id),
                unitCosts = getBudgetCostsPersistence.getBudgetUnitCosts(it.id),
            ).toDataModel()
            val budgetTotalCost = getBudgetTotalCosts(it.id).totalCosts
            val budget = PartnerBudgetData(budgetOptions, coFinancing, budgetCosts, budgetTotalCost)
            val stateAid = partnerPersistence.getPartnerStateAid(partnerId = it.id)
            it.toDataModel(stateAid, budget)
        }.toSet()

        val sectionB =
            ProjectDataSectionB(partners, associatedOrganizationService.findAllByProjectId(projectId).toDataModel())

        val sectionC = projectDescriptionPersistence.getProjectDescription(projectId).toDataModel(
            workPackages = workPackagePersistence.getWorkPackagesWithAllDataByProjectId(projectId),
            results = resultPersistence.getResultsForProject(projectId, null)
        )

        val sectionE = with(projectLumpSumPersistence.getLumpSums(projectId)) {
            this.toDataModel(programmeLumpSumPersistence.getLumpSums(this.map { it.programmeLumpSumId }))
        }

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(
            sectionA, sectionB, sectionC, sectionE,
            lifecycleData = ProjectLifecycleData(status = project.projectStatus.status.toDataModel())
        )
    }

    private fun getBudgetTotalCosts(partnerId: Long): BudgetCostsCalculationResult {
        val budgetOptions = budgetOptionsPersistence.getBudgetOptions(partnerId)
        val unitCostTotal = getBudgetCostsPersistence.getBudgetUnitCostTotal(partnerId)
        val lumpSumsTotal = getBudgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId)
        val equipmentCostTotal = getBudgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId)
        val externalCostTotal = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)
        val infrastructureCostTotal = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)

        val travelCostTotal = if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId)
        else
            BigDecimal.ZERO

        val staffCostTotal = if (budgetOptions?.staffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetStaffCostTotal(partnerId)
        else
            BigDecimal.ZERO

        return budgetCostsCalculator.calculateCosts(
            budgetOptions,
            unitCostTotal,
            lumpSumsTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal
        )
    }
}
