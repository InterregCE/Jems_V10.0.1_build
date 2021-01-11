package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageInvestmentApi
import io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment.AddWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment.DeleteWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment_ids_of_project.GetWorkPackageInvestmentIdsOfProjectInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments.GetWorkPackageInvestmentsInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestment
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageInvestmentController(
    private val getWorkPackageInvestments: GetWorkPackageInvestmentsInteractor,
    private val addWorkPackageInvestment: AddWorkPackageInvestmentInteractor,
    private val updateWorkPackageInvestment: UpdateWorkPackageInvestment,
    private val getWorkPackageInvestment: GetWorkPackageInvestmentInteractor,
    private val deleteWorkPackageInvestment: DeleteWorkPackageInvestmentInteractor,
    private val getWorkPackageInvestmentIdsOfProject: GetWorkPackageInvestmentIdsOfProjectInteractor

) : ProjectWorkPackageInvestmentApi {

    override fun getWorkPackageInvestment(investmentId: Long) =
        getWorkPackageInvestment.getWorkPackageInvestment(investmentId).toWorkPackageInvestmentDTO()

    override fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable) =
        getWorkPackageInvestments.getWorkPackageInvestments(workPackageId, pageable).toWorkPackageInvestmentDTOPage()

    override fun getWorkPackageInvestmentIdsOfProject(projectId: Long) =
        getWorkPackageInvestmentIdsOfProject.getWorkPackageInvestmentIds(projectId)

    override fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        addWorkPackageInvestment.addWorkPackageInvestment(workPackageId, workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        updateWorkPackageInvestment.updateWorkPackageInvestment(
            workPackageId,
            workPackageInvestmentDTO.toWorkPackageInvestment()
        )

    override fun deleteWorkPackageInvestment(workPackageId: Long, investmentId: Long) =
        deleteWorkPackageInvestment.deleteWorkPackageInvestment(workPackageId, investmentId)

}
