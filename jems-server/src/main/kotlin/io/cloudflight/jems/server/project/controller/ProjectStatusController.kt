package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectStatusApi
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.server.project.service.ProjectStatusService
import io.cloudflight.jems.server.project.service.application.approve_application.ApproveApplicationInteractor
import io.cloudflight.jems.server.project.service.application.approve_application_with_conditions.ApproveApplicationWithConditionsInteractor
import io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to.GetPossibleStatusToRevertToInteractor
import io.cloudflight.jems.server.project.service.application.refuse_application.RefuseApplicationInteractor
import io.cloudflight.jems.server.project.service.application.return_application_to_applicant.ReturnApplicationToApplicantInteractor
import io.cloudflight.jems.server.project.service.application.revert_application_decision.RevertApplicationDecisionInteractor
import io.cloudflight.jems.server.project.service.application.set_application_as_eligible.SetApplicationAsEligibleInteractor
import io.cloudflight.jems.server.project.service.application.set_application_as_ineligible.SetApplicationAsIneligibleInteractor
import io.cloudflight.jems.server.project.service.application.submit_application.SubmitApplicationInteractor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectStatusController(
    private val projectStatusService: ProjectStatusService,
    private val submitApplication: SubmitApplicationInteractor,
    private val approveApplication: ApproveApplicationInteractor,
    private val approveApplicationWithConditions: ApproveApplicationWithConditionsInteractor,
    private val setApplicationAsEligible: SetApplicationAsEligibleInteractor,
    private val setApplicationAsIneligible: SetApplicationAsIneligibleInteractor,
    private val returnApplicationToApplicant: ReturnApplicationToApplicantInteractor,
    private val revertApplicationDecision: RevertApplicationDecisionInteractor,
    private val getPossibleStatusToRevertTo: GetPossibleStatusToRevertToInteractor,
    private val refuseApplication: RefuseApplicationInteractor
) : ProjectStatusApi {

    override fun submitApplication(id: Long) =
        submitApplication.submit(id).toDTO()

    override fun setApplicationAsEligible(id: Long, actionInfo: ApplicationActionInfoDTO) =
        setApplicationAsEligible.setAsEligible(id, actionInfo.toModel()).toDTO()

    override fun setApplicationAsIneligible(id: Long, actionInfo: ApplicationActionInfoDTO) =
        setApplicationAsIneligible.setAsIneligible(id, actionInfo.toModel()).toDTO()

    override fun approveApplication(id: Long, actionInfo: ApplicationActionInfoDTO) =
        approveApplication.approve(id, actionInfo.toModel()).toDTO()

    override fun approveApplicationWithCondition(id: Long, actionInfo: ApplicationActionInfoDTO) =
        approveApplicationWithConditions.approveWithConditions(id, actionInfo.toModel()).toDTO()

    override fun refuseApplication(id: Long, actionInfo: ApplicationActionInfoDTO) =
        refuseApplication.refuse(id, actionInfo.toModel()).toDTO()

    override fun returnApplicationToApplicant(id: Long) =
        returnApplicationToApplicant.returnToApplicant(id).toDTO()

    override fun findPossibleDecisionRevertStatus(id: Long): ApplicationStatusDTO? =
        getPossibleStatusToRevertTo.get(projectId = id)?.toDTO()

    override fun revertApplicationDecision(id: Long): ApplicationStatusDTO =
        revertApplicationDecision.revert(id).toDTO()

    @PreAuthorize("@projectStatusAuthorization.canSetQualityAssessment(#id)")
    override fun setQualityAssessment(id: Long, data: InputProjectQualityAssessment): ProjectDetailDTO {
        return projectStatusService.setQualityAssessment(id, data)
    }

    @PreAuthorize("@projectStatusAuthorization.canSetEligibilityAssessment(#id)")
    override fun setEligibilityAssessment(id: Long, data: InputProjectEligibilityAssessment): ProjectDetailDTO {
        return projectStatusService.setEligibilityAssessment(id, data)
    }

}
