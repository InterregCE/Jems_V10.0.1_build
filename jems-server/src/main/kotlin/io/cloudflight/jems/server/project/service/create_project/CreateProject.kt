package io.cloudflight.jems.server.project.service.create_project

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import io.cloudflight.jems.server.project.authorization.CanCreateProject
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.callAlreadyEnded
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectApplicationCreated
import io.cloudflight.jems.server.project.service.projectVersionRecorded
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.MANAGE
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class CreateProject(
    private val persistence: ProjectPersistence,
    private val callPersistence: CallPersistence,
    private val collaboratorPersistence: UserProjectCollaboratorPersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
    private val programmeService: ProgrammeDataService,
    private val createNewProjectVersion: CreateNewProjectVersionInteractor
    ) : CreateProjectInteractor {

    @CanCreateProject
    @Transactional
    @ExceptionWrapper(CreateProjectExceptions::class)
    override fun createProject(acronym: String, callId: Long): ProjectDetail {
        validateProjectAcronym(acronym)

        val call = callPersistence.getCallById(callId)
        if (!call.isPublished())
            throw CallNotFound()

        val now = ZonedDateTime.now()
        if (call.getCallApplyDeadline().isBefore(now) || call.startDate.isAfter(now)) {
            auditPublisher.publishEvent(callAlreadyEnded(this, call))
            throw CallNotOpen()
        }

        val userId = securityService.currentUser?.user?.id!!
        val status = if (call.is2StepCall()) ApplicationStatus.STEP1_DRAFT else ApplicationStatus.DRAFT
        val project = persistence.createProjectWithStatus(acronym = acronym, status = status, userId = userId, callId = callId)
        // create default collaborator from applicant
        collaboratorPersistence.changeUsersAssignedToProject(project.id!!, mapOf(userId to MANAGE))

        val projectIdPrefix = getProjectIdPrefix(callId = callId)
        val customIdentifier = getCustomIdentifierForProjectId(prefix = projectIdPrefix, project.id)
        persistence.updateProjectCustomIdentifier(project.id, customIdentifier)

        auditPublisher.publishEvent(projectApplicationCreated(this, project))

        createNewProjectVersion.create(project.id)
        auditPublisher.publishEvent(
            projectVersionRecorded(
                context = this,
                projectSummary = ProjectSummary(project.id, customIdentifier, call.id, call.name, project.acronym, project.projectStatus.status),
                userEmail = project.applicant.email,
            )
        )

        return project
    }

    private fun validateProjectAcronym(acronym: String) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notBlank(acronym, "acronym"),
            generalValidator.maxLength(acronym, 25, "acronym"),
        )

    private fun getCustomIdentifierForProjectId(prefix: String, projectId: Long): String =
        "${prefix}%05d".format(projectId)

    private fun getProjectIdPrefix(callId: Long): String {
        val setup = programmeService.get()
        val callIdPart = if (setup.projectIdUseCallId) "%02d".format(callId) else ""

        return "${setup.projectIdProgrammeAbbreviation.orEmpty()}$callIdPart"
    }

}
