package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.ProjectStatusApi
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.approve_application.ApproveApplicationInteractor
import io.cloudflight.jems.server.project.service.application.approve_application_with_conditions.ApproveApplicationWithConditionsInteractor
import io.cloudflight.jems.server.project.service.application.approve_modification.ApproveModificationInteractor
import io.cloudflight.jems.server.project.service.application.execute_pre_condition_check.ExecutePreConditionCheckInteractor
import io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to.GetPossibleStatusToRevertToInteractor
import io.cloudflight.jems.server.project.service.application.hand_back_to_applicant.HandBackToApplicantInteractor
import io.cloudflight.jems.server.project.service.application.refuse_application.RefuseApplicationInteractor
import io.cloudflight.jems.server.project.service.application.reject_modification.RejectModificationInteractor
import io.cloudflight.jems.server.project.service.application.return_application_to_applicant.ReturnApplicationToApplicantInteractor
import io.cloudflight.jems.server.project.service.application.revert_application_decision.RevertApplicationDecisionInteractor
import io.cloudflight.jems.server.project.service.application.set_application_as_eligible.SetApplicationAsEligibleInteractor
import io.cloudflight.jems.server.project.service.application.set_application_as_ineligible.SetApplicationAsIneligibleInteractor
import io.cloudflight.jems.server.project.service.application.set_application_to_contracted.SetApplicationToContracted
import io.cloudflight.jems.server.project.service.application.set_application_to_contracted.SetApplicationToContractedInteractor
import io.cloudflight.jems.server.project.service.application.set_assessment_eligibility.SetAssessmentEligibilityInteractor
import io.cloudflight.jems.server.project.service.application.set_assessment_quality.SetAssessmentQualityInteractor
import io.cloudflight.jems.server.project.service.application.start_modification.StartModificationInteractor
import io.cloudflight.jems.server.project.service.application.start_second_step.StartSecondStepInteractor
import io.cloudflight.jems.server.project.service.application.submit_application.SubmitApplicationInteractor
import io.cloudflight.jems.server.project.service.get_modification_decisions.GetModificationDecisionsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectStatusController(
    private val executePreConditionCheck: ExecutePreConditionCheckInteractor,
    private val submitApplication: SubmitApplicationInteractor,
    private val setApplicationAsEligible: SetApplicationAsEligibleInteractor,
    private val setApplicationAsIneligible: SetApplicationAsIneligibleInteractor,
    private val approveApplication: ApproveApplicationInteractor,
    private val approveModification: ApproveModificationInteractor,
    private val rejectModification: RejectModificationInteractor,
    private val approveApplicationWithConditions: ApproveApplicationWithConditionsInteractor,
    private val refuseApplication: RefuseApplicationInteractor,
    private val returnApplicationToApplicant: ReturnApplicationToApplicantInteractor,
    private val startModification: StartModificationInteractor,
    private val handBackToApplicant: HandBackToApplicantInteractor,
    private val startSecondStep: StartSecondStepInteractor,
    private val getPossibleStatusToRevertTo: GetPossibleStatusToRevertToInteractor,
    private val revertApplicationDecision: RevertApplicationDecisionInteractor,
    private val setAssessmentEligibilityInteractor: SetAssessmentEligibilityInteractor,
    private val setAssessmentQualityInteractor: SetAssessmentQualityInteractor,
    private val getModificationDecisionsInteractor: GetModificationDecisionsInteractor,
    private val setApplicationToContracted: SetApplicationToContractedInteractor
) : ProjectStatusApi {
    override fun preConditionCheck(id: Long): PreConditionCheckResultDTO =
        executePreConditionCheck.execute(id).toDTO()

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

    override fun startModification(id: Long) =
        startModification.startModification(id).toDTO()

    override fun getModificationDecisions(id: Long): List<ProjectStatusDTO> =
        getModificationDecisionsInteractor.getModificationDecisions(id).toDtos()

    override fun handBackToApplicant(id: Long) =
        handBackToApplicant.handBackToApplicant(id).toDTO()

    override fun startSecondStep(id: Long): ApplicationStatusDTO =
        startSecondStep.startSecondStep(id).toDTO()

    override fun findPossibleDecisionRevertStatus(id: Long): ApplicationStatusDTO? =
        getPossibleStatusToRevertTo.get(projectId = id)?.toDTO()

    override fun revertApplicationDecision(id: Long): ApplicationStatusDTO =
        revertApplicationDecision.revert(id).toDTO()

    override fun setQualityAssessment(id: Long, data: ProjectAssessmentQualityDTO): ProjectDetailDTO =
        setAssessmentQualityInteractor.setQualityAssessment(id, data.result, data.note).toDto()

    override fun setEligibilityAssessment(id: Long, data: ProjectAssessmentEligibilityDTO): ProjectDetailDTO =
        setAssessmentEligibilityInteractor.setEligibilityAssessment(id, data.result, data.note).toDto()

    override fun approveModification(id: Long, actionInfo: ApplicationActionInfoDTO): ApplicationStatusDTO =
        approveModification.approveModification(id, actionInfo.toModel()).toDTO()

    override fun rejectModification(id: Long, actionInfo: ApplicationActionInfoDTO): ApplicationStatusDTO =
        rejectModification.reject(id, actionInfo.toModel()).toDTO()

    override fun setToContracted(id: Long): ApplicationStatusDTO =
        setApplicationToContracted.setApplicationToContracted(id).toDTO()

}
