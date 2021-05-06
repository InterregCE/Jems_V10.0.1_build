package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.project.service.ProjectDescriptionService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums.GetProjectLumpSumsInteractor
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectDataProviderImpl(
    private val projectService: ProjectService,
    private val projectDescriptionService: ProjectDescriptionService,
    private val workPackagePersistence: WorkPackagePersistence,
    private val resultPersistence: ProjectResultPersistence,
    private val projectPartnerService: ProjectPartnerService,
    private val associatedOrganizationService: ProjectAssociatedOrganizationService,
    private val getBudgetOptions: GetBudgetOptionsInteractor,
    private val getCoFinancing: GetCoFinancingInteractor,
    private val getBudgetCosts: GetBudgetCostsInteractor,
    private val getBudgetTotalCost: GetBudgetTotalCostInteractor,
    private val getProjectLumpSumsInteractor: GetProjectLumpSumsInteractor
) : ProjectDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectDataProviderImpl::class.java)
        private const val MAX_WORK_PACKAGES_PER_PROJECT = 20
    }

    @Transactional(readOnly = true)
    override fun getProjectDataForProjectId(projectId: Long): ProjectData {
        val sectionA = projectService.getById(projectId).projectData?.toDataModel()

        val partners = projectPartnerService.findAllByProjectId(projectId).map {
            val budgetOptions = getBudgetOptions.getBudgetOptions(it.id!!)?.toDataModel()
            val coFinancing = getCoFinancing.getCoFinancing(it.id!!).toDataModel()
            val budgetCosts = getBudgetCosts.getBudgetCosts(it.id!!).toDataModel()
            val budgetTotalCost = getBudgetTotalCost.getBudgetTotalCost(it.id!!)
            val budget = PartnerBudgetData(budgetOptions, coFinancing, budgetCosts, budgetTotalCost)
            it.toDataModel(budget)
        }.toSet()
        val associatedOrganisations = associatedOrganizationService.findAllByProjectId(projectId).map { it.toDataModel() }.toSet()
        val sectionB = ProjectDataSectionB(partners, associatedOrganisations)

        val workPackages = workPackagePersistence.getRichWorkPackagesByProjectId(projectId, PageRequest.of(0, MAX_WORK_PACKAGES_PER_PROJECT)).content.toDataModel()
        val results = resultPersistence.getResultsForProject(projectId).toResultDataModel()
        val sectionC = projectDescriptionService.getProjectDescription(projectId).toDataModel(workPackages, results)

        val lumpSums = getProjectLumpSumsInteractor.getLumpSums(projectId).map { it.toDataModel() }.toList()
        val sectionE = ProjectDataSectionE(lumpSums)

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(sectionA, sectionB, sectionC, sectionE)
    }
}