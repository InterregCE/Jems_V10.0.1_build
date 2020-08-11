package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.APPROVED
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.NOT_APPROVED
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotFinallyFunded
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotSubmittedNow
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.Companion.wasSubmittedAtLeastOnce
import io.cloudflight.ems.api.dto.ProjectFileType
import io.cloudflight.ems.api.dto.ProjectFileType.APPLICANT_FILE
import io.cloudflight.ems.api.dto.ProjectFileType.ASSESSMENT_FILE
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.authorization.ProjectAuthorization
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.FileStorageService
import io.cloudflight.ems.project.service.ProjectService
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class ProjectFileAuthorization(
    override val securityService: SecurityService,
    val projectService: ProjectService,
    val fileStorageService: FileStorageService,
    val projectAuthorization: ProjectAuthorization
): Authorization(securityService) {

    fun canUploadFile(projectId: Long, fileType: ProjectFileType): Boolean {
        if (isAdmin())
            return true

        projectAuthorization.canReadProject(projectId)
        val project = projectService.getById(projectId)
        val status = project.projectStatus.status

        return when (fileType) {
            APPLICANT_FILE -> isOwner(project) && isNotSubmittedNow(status)
            ASSESSMENT_FILE -> isProgrammeUser()
            else -> false
        }
    }

    fun canChangeFile(projectId: Long, fileId: Long): Boolean {
        if (isAdmin())
            return true

        projectAuthorization.canReadProject(projectId)
        val project = projectService.getById(projectId)
        val file = fileStorageService.getFileDetail(projectId, fileId)

        return when (file.type) {
            APPLICANT_FILE -> canChangeApplicantProjectFile(project, file)
            ASSESSMENT_FILE -> canChangeAssessmentProjectFile(project, file)
            else -> false
        }
    }

    private fun canChangeApplicantProjectFile(project: OutputProject, file: OutputProjectFile): Boolean {
        val status = project.projectStatus.status
        if (isOwner(project))
            return isNotSubmittedNow(status) && file.updated.isAfter(getLastSubmissionFor(project))
        else
            throw ResourceNotFoundException("project_file")
    }

    private fun canChangeAssessmentProjectFile(project: OutputProject, file: OutputProjectFile): Boolean {
        val status = project.projectStatus.status
        if (isProgrammeUser()) {
            return when {

                isNotFinallyFunded(status) ->
                    wasSubmittedAtLeastOnce(status)

                status == APPROVED || status == NOT_APPROVED ->
                    file.updated.isAfter(project.fundingDecision!!.updated)

                status == INELIGIBLE ->
                    file.updated.isAfter(project.eligibilityDecision!!.updated)

                else -> false
            }
        } else
            throw ResourceNotFoundException("project_file")
    }

    fun canDownloadFile(projectId: Long, fileId: Long): Boolean {
        if (isAdmin())
            return true

        projectAuthorization.canReadProject(projectId)
        val project = projectService.getById(projectId)
        val file = fileStorageService.getFileDetail(projectId, fileId)

        if (isOwner(project))
            if (file.type == APPLICANT_FILE)
                return true
            else
                throw ResourceNotFoundException("project_file")

        if (isProgrammeUser())
            return true

        return false
    }

    fun canListFiles(projectId: Long, fileType: ProjectFileType): Boolean {
        if (isAdmin())
            return true

        projectAuthorization.canReadProject(projectId)

        return when (fileType) {
            ASSESSMENT_FILE -> isProgrammeUser()
            APPLICANT_FILE -> isProgrammeUser() || isOwner(projectService.getById(projectId))
            else -> false
        }
    }

    /**
     * take last resubmission, if not then submission, if not then it means it is a DRAFT so take status timestamp
     */
    fun getLastSubmissionFor(project: OutputProject): ZonedDateTime {
        return project.lastResubmission?.updated
            ?: project.firstSubmission?.updated
            ?: project.projectStatus.updated
    }

}
