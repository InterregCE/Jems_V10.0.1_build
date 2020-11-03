package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.APPROVED
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.NOT_APPROVED
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotFinallyFunded
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotSubmittedNow
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.Companion.wasSubmittedAtLeastOnce
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.api.project.dto.file.ProjectFileType.APPLICANT_FILE
import io.cloudflight.jems.api.project.dto.file.ProjectFileType.ASSESSMENT_FILE
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.FileStorageService
import io.cloudflight.jems.server.project.service.ProjectService
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
        val project = projectService.getApplicantAndStatusById(projectId)

        return when (fileType) {
            APPLICANT_FILE -> isApplicantOwner(project.applicantId) && isNotSubmittedNow(project.projectStatus)
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
        if (isApplicantOwner(project.applicant.id!!))
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
        val project = projectService.getApplicantAndStatusById(projectId)
        val file = fileStorageService.getFileDetail(projectId, fileId)

        if (isApplicantOwner(project.applicantId))
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
            APPLICANT_FILE -> isProgrammeUser() || isApplicantOwner(projectService.getApplicantAndStatusById(projectId).applicantId)
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
