package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageInvestmentApi
import io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment.AddWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment.DeleteWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments.GetWorkPackageInvestmentsInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestment
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ProjectWorkPackageInvestmentController(
    private val getWorkPackageInvestmentsInteractor: GetWorkPackageInvestmentsInteractor,
    private val addWorkPackageInvestmentInteractor: AddWorkPackageInvestmentInteractor,
    private val updateWorkPackageInvestmentInteractor: UpdateWorkPackageInvestment,
    private val getWorkPackageInvestmentInteractor: GetWorkPackageInvestmentInteractor,
    private val deleteWorkPackageInvestmentInteractor: DeleteWorkPackageInvestmentInteractor

) : ProjectWorkPackageInvestmentApi {

    override fun getWorkPackageInvestment(investmentId: UUID) =
        getWorkPackageInvestmentInteractor.getWorkPackageInvestment(investmentId).toWorkPackageInvestmentDTO()

    override fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable) =
        getWorkPackageInvestmentsInteractor.getWorkPackageInvestments(workPackageId, pageable).toWorkPackageInvestmentDTOPage()

    override fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        addWorkPackageInvestmentInteractor.addWorkPackageInvestment(workPackageId, workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun updateWorkPackageInvestment(workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        updateWorkPackageInvestmentInteractor.updateWorkPackageInvestment(workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun deleteWorkPackageInvestment(investmentId: UUID) =
        deleteWorkPackageInvestmentInteractor.deleteWorkPackageInvestment(investmentId)

}
