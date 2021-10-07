package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
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
    private val associatedOrganizationPersistence: AssociatedOrganizationPersistence,
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
    override fun getProjectDataForProjectId(projectId: Long, version: String?): ProjectData {
        val project = projectPersistence.getProject(projectId, version)
        val sectionA = project.toDataModel()

        val partners = partnerPersistence.findTop30ByProjectId(projectId, version).map {
            val budgetOptions = budgetOptionsPersistence.getBudgetOptions(it.id, version)
            val coFinancing = coFinancingPersistence.getCoFinancingAndContributions(it.id, version).toDataModel()
            val budgetCosts = BudgetCosts(
                staffCosts = getBudgetCostsPersistence.getBudgetStaffCosts(it.id, version),
                travelCosts = getBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(it.id, version),
                externalCosts = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(it.id, version),
                equipmentCosts = getBudgetCostsPersistence.getBudgetEquipmentCosts(it.id, version),
                infrastructureCosts = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(it.id, version),
                unitCosts = getBudgetCostsPersistence.getBudgetUnitCosts(it.id, version),
            ).toDataModel()
            val budgetTotalCost = getBudgetTotalCosts(budgetOptions, it.id, version).totalCosts
            val budget = PartnerBudgetData(budgetOptions?.toDataModel(), coFinancing, budgetCosts, budgetTotalCost)
            val stateAid = partnerPersistence.getPartnerStateAid(partnerId = it.id, version)
            it.toDataModel(stateAid, budget)
        }.toSet()

        val sectionB =
            ProjectDataSectionB(partners, associatedOrganizationPersistence.findAllByProjectId(projectId, version).toDataModel())

        val sectionC = projectDescriptionPersistence.getProjectDescription(projectId, version).toDataModel(
            workPackages = workPackagePersistence.getWorkPackagesWithAllDataByProjectId(projectId, version),
            results = resultPersistence.getResultsForProject(projectId, version)
        )

        val sectionE = with(projectLumpSumPersistence.getLumpSums(projectId, version)) {
            this.toDataModel(programmeLumpSumPersistence.getLumpSums(this.map { it.programmeLumpSumId }))
        }

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(
            sectionA, sectionB, sectionC, sectionE,
            lifecycleData = ProjectLifecycleData(status = project.projectStatus.status.toDataModel())
        )
    }

    private fun getBudgetTotalCosts(budgetOptions: ProjectPartnerBudgetOptions?, partnerId: Long, version: String?): BudgetCostsCalculationResult {
        val unitCostTotal = getBudgetCostsPersistence.getBudgetUnitCostTotal(partnerId, version)
        val lumpSumsTotal = getBudgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId, version)
        val equipmentCostTotal = getBudgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId, version)
        val externalCostTotal = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId, version)
        val infrastructureCostTotal = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId, version)

        val travelCostTotal = if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId, version)
        else
            BigDecimal.ZERO

        val staffCostTotal = if (budgetOptions?.staffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetStaffCostTotal(partnerId, version)
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
