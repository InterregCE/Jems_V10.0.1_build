package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.WorkPackageApi
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputUpdateDTO
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.project.service.workpackage.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment.AddWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment.DeleteWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments.GetWorkPackageInvestmentsInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.update_work_package_output.UpdateWorkPackageOutputInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class WorkPackageController(
    private val workPackageService: WorkPackageService,
    private val getWorkPackageOutputInteractor: GetWorkPackageOutputInteractor,
    private val updateWorkPackageOutputInteractor: UpdateWorkPackageOutputInteractor,
    private val getWorkPackageInvestmentsInteractor: GetWorkPackageInvestmentsInteractor,
    private val addWorkPackageInvestmentInteractor: AddWorkPackageInvestmentInteractor,
    private val updateWorkPackageInvestmentInteractor: UpdateWorkPackageInvestment,
    private val getWorkPackageInvestmentInteractor: GetWorkPackageInvestmentInteractor,
    private val deleteWorkPackageInvestmentInteractor: DeleteWorkPackageInvestmentInteractor

) : WorkPackageApi {

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getWorkPackageById(projectId: Long, id: Long): OutputWorkPackage {
        return workPackageService.getWorkPackageById(id)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple> {
        return workPackageService.getWorkPackagesByProjectId(projectId, pageable)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        return workPackageService.createWorkPackage(projectId, inputWorkPackageCreate)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        return workPackageService.updateWorkPackage(projectId, inputWorkPackageUpdate)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun deleteWorkPackage(projectId: Long, id: Long) {
        return workPackageService.deleteWorkPackage(projectId, id)
    }

    override fun getWorkPackageOutputs(projectId: Long, id: Long): Set<WorkPackageOutputDTO> {
        return getWorkPackageOutputInteractor.getWorkPackageOutputsForWorkPackage(projectId, id)
            .toWorkPackageOutputDTOSet()
    }

    override fun updateWorkPackageOutputs(
        projectId: Long,
        id: Long,
        workPackageOutputUpdateDTO: Set<WorkPackageOutputUpdateDTO>
    ): Set<WorkPackageOutputDTO> =
        updateWorkPackageOutputInteractor.updateWorkPackageOutputs(
            projectId,
            workPackageOutputUpdateDTO.toWorkPackageOutputUpdateSet(),
            id
        ).toWorkPackageOutputDTOSet()

    override fun getWorkPackageInvestment(projectId: Long, id: Long, investmentId: UUID) =
        getWorkPackageInvestmentInteractor.getWorkPackageInvestment(projectId, investmentId).toWorkPackageInvestmentDTO()

    override fun getWorkPackageInvestments(projectId: Long, id: Long, pageable: Pageable) =
        getWorkPackageInvestmentsInteractor.getWorkPackageInvestments(projectId, id, pageable).toWorkPackageInvestmentDTOPage()

    override fun addWorkPackageInvestment(projectId: Long, id: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        addWorkPackageInvestmentInteractor.addWorkPackageInvestment(projectId,id, workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun updateWorkPackageInvestment(projectId: Long, id: Long, workPackageInvestmentDTO: WorkPackageInvestmentDTO) =
        updateWorkPackageInvestmentInteractor.updateWorkPackageInvestment(projectId, workPackageInvestmentDTO.toWorkPackageInvestment())

    override fun deleteWorkPackageInvestment(projectId: Long, id: Long, investmentId: UUID) =
        deleteWorkPackageInvestmentInteractor.deleteWorkPackageInvestment(projectId,investmentId)

}
