package io.cloudflight.jems.server.dataGenerator.project

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_DURATION
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_INVESTMENTS
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_LP
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_PP
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_WORK_PACKAGES
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_DURATION
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_INVESTMENTS
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_LP
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_PP
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_WORK_PACKAGES
import io.cloudflight.jems.server.project.controller.partner.toModel
import io.cloudflight.jems.server.project.controller.result.toModel
import io.cloudflight.jems.server.project.controller.toModel
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.partner.update_project_partner.UpdateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.result.update_project_results.UpdateProjectResultsInteractor
import io.cloudflight.jems.server.project.service.update_project_description.UpdateProjectDescriptionInteractor
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageServiceImpl
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.UpdateActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor


class ApplicationFormDataUpdateHelper(
    private val projectService: ProjectService,
    private val updateProjectPartner: UpdateProjectPartnerInteractor,
    private val updateProjectDescription: UpdateProjectDescriptionInteractor,
    private val updateProjectResults: UpdateProjectResultsInteractor,
    private val updateProjectWorkPackage: WorkPackageServiceImpl,
    private val updateActivity: UpdateActivityInteractor,
    private val updateInvestment: UpdateWorkPackageInvestmentInteractor,
    private val updateWorkPackageOutput: UpdateWorkPackageOutputInteractor
) {


    fun updateVersionInFormInputs(projectId: Long, version: String) {
        updateVersionInSectionAInputs(projectId, version)
        updateVersionInSectionBInputs(projectId, version)
        updateVersionInSectionCInputs(projectId, version)
        updateVersionInSectionCDataWorkPlanInputs(projectId, version)
    }

    private fun updateVersionInSectionAInputs(projectId: Long, version: String) =
        projectService.update(projectId, inputProjectData(version, getProjectDuration(projectId)))

    private fun updateVersionInSectionBInputs(projectId: Long, version: String) =
        getPartnersByProjectId(projectId).forEach {
            updateProjectPartner.update(
                projectPartnerDTO(it.id, version, it.abbreviation.substringAfter("- "), it.role).toModel()
            )
        }

    private fun updateVersionInSectionCInputs(projectId: Long, version: String) {
        updateProjectDescription.updateOverallObjective(projectId, inputProjectOverallObjective(version).toModel())
        updateProjectDescription.updateProjectRelevance(projectId, inputProjectRelevance(version).toModel())
        updateProjectDescription.updatePartnership(projectId, inputProjectPartnership(version).toModel())
        updateProjectResults.updateResultsForProject(
            projectId, listOf(projectResultUpdateRequestDTO(version)).toModel()
        )
        updateProjectDescription.updateProjectManagement(projectId, inputProjectManagement(version).toModel())
        updateProjectDescription.updateProjectLongTermPlans(projectId, inputProjectLongTermPlans(version).toModel())
    }

    private fun updateVersionInSectionCDataWorkPlanInputs(projectId: Long, version: String) {
        getWorkPlansByProjectId(projectId).forEach { workPackage ->
            updateProjectWorkPackage.updateWorkPackage(projectId, inputWorkPackageUpdate(workPackage.id, version))
            updateActivity.updateActivitiesForWorkPackage(
                projectId, workPackage.id,
                workPackage.activities.map { workPackageActivity(it, workPackage.workPackageNumber, version) }
            )
            updateWorkPackageOutput.updateOutputsForWorkPackage(
                projectId, workPackage.id, workPackage.outputs.map { outputDTO ->
                    workPackageOutput(workPackage.id, outputDTO, version)
                }
            )
            getWorkPlansInvestments(projectId).forEach { investment ->
                updateInvestment.updateWorkPackageInvestment(
                    projectId, workPackage.id, workPackageInvestment(investment, version)
                )
            }
        }
    }

    private fun getPartnersByProjectId(projectId: Long): List<ProjectPartnerDetailDTO> =
        when (projectId) {
            CONTRACTED_PROJECT_ID -> listOf(CONTRACTED_PROJECT_PP, CONTRACTED_PROJECT_LP)
            DRAFT_PROJECT_ID -> listOf(DRAFT_PROJECT_PP, DRAFT_PROJECT_LP)
            else -> emptyList()
        }

    private fun getWorkPlansByProjectId(projectId: Long): List<ProjectWorkPackageDTO> =
        when (projectId) {
            CONTRACTED_PROJECT_ID -> CONTRACTED_PROJECT_WORK_PACKAGES
            DRAFT_PROJECT_ID -> DRAFT_PROJECT_WORK_PACKAGES
            else -> emptyList()
        }

    private fun getWorkPlansInvestments(projectId: Long): List<WorkPackageInvestmentDTO> =
        when (projectId) {
            CONTRACTED_PROJECT_ID -> CONTRACTED_PROJECT_INVESTMENTS
            DRAFT_PROJECT_ID -> DRAFT_PROJECT_INVESTMENTS
            else -> emptyList()
        }

    private fun getProjectDuration(projectId: Long): Int =
        when (projectId) {
            CONTRACTED_PROJECT_ID -> CONTRACTED_PROJECT_DURATION
            DRAFT_PROJECT_ID -> DRAFT_PROJECT_DURATION
            else -> 0
        }

}
