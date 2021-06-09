package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageInvestmentApi
import io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment.AddWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment.DeleteWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_project_investment_summaries.GetProjectInvestmentSummariesInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments.GetWorkPackageInvestmentsInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestment
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageInvestmentController(
    private val getWorkPackageInvestments: GetWorkPackageInvestmentsInteractor,
    private val addWorkPackageInvestment: AddWorkPackageInvestmentInteractor,
    private val updateWorkPackageInvestment: UpdateWorkPackageInvestment,
    private val getWorkPackageInvestment: GetWorkPackageInvestmentInteractor,
    private val deleteWorkPackageInvestment: DeleteWorkPackageInvestmentInteractor,
    private val getProjectInvestmentSummaries: GetProjectInvestmentSummariesInteractor

) : ProjectWorkPackageInvestmentApi {

    override fun getWorkPackageInvestment(investmentId: Long, projectId: Long, workPackageId: Long, version: String?) =
        getWorkPackageInvestment.getWorkPackageInvestment(projectId, investmentId, version).toWorkPackageInvestmentDTO()

    override fun getWorkPackageInvestments(projectId: Long, workPackageId: Long, version: String?) =
        getWorkPackageInvestments.getWorkPackageInvestments(projectId, workPackageId, version).toWorkPackageInvestmentDTOList()

    override fun getProjectInvestmentSummaries(projectId: Long, workPackageId: Long, version: String?) =
        getProjectInvestmentSummaries.getProjectInvestmentSummaries(projectId, version).toInvestmentSummaryDTOs()

    override fun addWorkPackageInvestment(projectId: Long, workPackageId: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        addWorkPackageInvestment.addWorkPackageInvestment(projectId, workPackageId, workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun updateWorkPackageInvestment(projectId: Long, workPackageId: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        updateWorkPackageInvestment.updateWorkPackageInvestment(
            projectId,
            workPackageId,
            workPackageInvestmentDTO.toWorkPackageInvestment()
        )

    override fun deleteWorkPackageInvestment(investmentId: Long, projectId: Long, workPackageId: Long) =
        deleteWorkPackageInvestment.deleteWorkPackageInvestment(projectId, workPackageId, investmentId)

}
