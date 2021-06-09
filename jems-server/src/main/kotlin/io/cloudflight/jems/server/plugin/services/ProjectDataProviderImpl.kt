package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    private val getBudgetCosts: GetBudgetCostsInteractor,
    private val getBudgetTotalCost: GetBudgetTotalCostInteractor,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence
) : ProjectDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getProjectDataForProjectId(projectId: Long): ProjectData {
        val sectionA = projectPersistence.getProject(projectId).toDataModel()

        val partners = partnerPersistence.findAllByProjectId(projectId).map {
            val budgetOptions = budgetOptionsPersistence.getBudgetOptions(it.id!!)?.toDataModel()
            val coFinancing = coFinancingPersistence.getCoFinancingAndContributions(it.id!!, null).toDataModel()
            //TODO getBudgetCosts should be replaced by persistence/service call without permissions
            val budgetCosts = getBudgetCosts.getBudgetCosts(it.id!!).toDataModel()
            //TODO getBudgetTotalCost should be replaced by persistence/service call without permissions
            val budgetTotalCost = getBudgetTotalCost.getBudgetTotalCost(it.id!!)
            val budget = PartnerBudgetData(budgetOptions, coFinancing, budgetCosts, budgetTotalCost)
            it.toDataModel(budget)
        }.toSet()
        val associatedOrganisations =
            associatedOrganizationService.findAllByProjectId(projectId).map { it.toDataModel() }.toSet()
        val sectionB = ProjectDataSectionB(partners, associatedOrganisations)

        val workPackages = workPackagePersistence.getWorkPackagesWithAllDataByProjectId(projectId).toDataModel()
        val results = resultPersistence.getResultsForProject(projectId).toResultDataModel()
        val sectionC = projectDescriptionPersistence.getProjectDescription(projectId).toDataModel(workPackages, results)

        val projectLumpSums = projectLumpSumPersistence.getLumpSums(projectId)
        val lumpSumsDetail = programmeLumpSumPersistence.getLumpSums(projectLumpSums.map { it.programmeLumpSumId })
        val sectionE = ProjectDataSectionE(projectLumpSums.map { it.toDataModel(lumpSumsDetail) }.toList())

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(sectionA, sectionB, sectionC, sectionE)
    }
}
