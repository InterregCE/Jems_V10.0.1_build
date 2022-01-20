package io.cloudflight.jems.server.project.service.application

import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.ConditionsSubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ContractedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.InEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationPreContractingApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationPreContractingSubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationRejectedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.NotApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantForConditionsApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.SubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepDraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepIneligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepNotApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepSubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.mockk
import java.time.ZonedDateTime

val callSettings = ProjectCallSettings(
    callId = 2L,
    callName = "call",
    startDate = ZonedDateTime.now(),
    endDate = ZonedDateTime.now(),
    lengthOfPeriod = 2,
    endDateStep1 = null,
    isAdditionalFundAllowed = false,
    flatRates = emptySet(),
    lumpSums = emptyList(),
    unitCosts = emptyList(),
    stateAids = emptyList(),
    applicationFormFieldConfigurations = mutableSetOf(),
    preSubmissionCheckPluginKey = null
)

fun projectWithId(id: Long, status: ApplicationStatus = ApplicationStatus.SUBMITTED) = ProjectFull(
    id = id,
    customIdentifier = "01",
    callSettings = callSettings,
    acronym = "project acronym",
    applicant = UserSummary(3L, "email", "name", "surname", UserRoleSummary(4L, "role"), UserStatus.ACTIVE),
    projectStatus = ProjectStatus(
        id = null,
        status = status,
        user = UserSummary(0, "", "", "", UserRoleSummary(name = ""), UserStatus.ACTIVE),
        updated = ZonedDateTime.now(),
    ),
    duration = 10,
)

fun listOfApplicationStates() =
    listOf(
        Pair(
            ApplicationStatus.STEP1_DRAFT,
            FirstStepDraftApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_SUBMITTED,
            FirstStepSubmittedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_ELIGIBLE,
            FirstStepEligibleApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_INELIGIBLE,
            FirstStepIneligibleApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_APPROVED,
            FirstStepApprovedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS,
            FirstStepApprovedApplicationWithConditionsState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.STEP1_NOT_APPROVED,
            FirstStepNotApprovedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.APPROVED,
            ApprovedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.APPROVED_WITH_CONDITIONS,
            ApprovedApplicationWithConditionsState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.DRAFT,
            DraftApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.ELIGIBLE,
            EligibleApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.INELIGIBLE,
            InEligibleApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.NOT_APPROVED,
            NotApprovedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.RETURNED_TO_APPLICANT,
            ReturnedToApplicantApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS,
            ReturnedToApplicantForConditionsApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.SUBMITTED,
            SubmittedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.CONDITIONS_SUBMITTED,
            ConditionsSubmittedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.MODIFICATION_PRECONTRACTING,
            ModificationPreContractingApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED,
            ModificationPreContractingSubmittedApplicationState(
                mockk(relaxed = true), mockk(), mockk(), mockk(), mockk(), mockk()
            )
        ),
        Pair(
            ApplicationStatus.MODIFICATION_REJECTED,
            ModificationRejectedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        ),
        Pair(
            ApplicationStatus.CONTRACTED,
            ContractedApplicationState(mockk(relaxed = true), mockk(), mockk(), mockk(), mockk())
        )
    )
