package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.Companion.isNotFinallyFunded
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.Companion.isNotSubmittedNow
import io.cloudflight.ems.api.dto.ProjectFileType
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.FileStorageService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class ProjectFileAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService,
    val fileStorageService: FileStorageService
) {

    fun canUploadFile(projectId: Long, fileType: ProjectFileType): Boolean {
        if (isAdmin())
            return true

        val project = projectService.getById(projectId)
        val status = project.projectStatus.status

        if (fileType == ProjectFileType.APPLICANT_FILE)
            return isOwner(project)
                && isNotSubmittedNow(status)

        if (fileType == ProjectFileType.ASSESSMENT_FILE)
            return isProgrammeUser()
                && status != DRAFT

        return false
    }

    fun canChangeFile(projectId: Long, fileId: Long): Boolean {
        if (isAdmin())
            return true

        val project = projectService.getById(projectId)
        val status = project.projectStatus.status
        val file = fileStorageService.getFileDetail(projectId, fileId)
        val lastSubmission = getLastSubmissionFor(project)

        if (file.type == ProjectFileType.APPLICANT_FILE)
            return isOwner(project)
                && isNotSubmittedNow(status)
                && file.updated.isAfter(lastSubmission)

        if (file.type == ProjectFileType.ASSESSMENT_FILE)
            return isProgrammeUser()
                && isNotFinallyFunded(status)

        return false
    }

    fun canDownloadFile(projectId: Long, fileId: Long): Boolean {
        if (isAdmin())
            return true

        val project = projectService.getById(projectId)
        val file = fileStorageService.getFileDetail(projectId, fileId)
        val status = project.projectStatus.status

        if (isOwner(project))
            return file.type == ProjectFileType.APPLICANT_FILE

        if (isProgrammeUser() && file.type == ProjectFileType.APPLICANT_FILE)
            return status != DRAFT

        if (isProgrammeUser() && file.type == ProjectFileType.ASSESSMENT_FILE)
            return true

        return false
    }

    fun canListFiles(projectId: Long, fileType: ProjectFileType): Boolean {
        if (isAdmin())
            return true

        if (fileType == ProjectFileType.ASSESSMENT_FILE)
            return isProgrammeUser()

        val project = projectService.getById(projectId)
        val status = project.projectStatus.status

        if (fileType == ProjectFileType.APPLICANT_FILE)
            return isOwner(project) || (isProgrammeUser() && status != DRAFT)

        return false
    }

    /**
     * take last resubmission, if not then submission, if not then it means it is a DRAFT so take status timestamp
     */
    fun getLastSubmissionFor(project: OutputProject): ZonedDateTime {
        return project.lastResubmission?.updated
            ?: project.firstSubmission?.updated
            ?: project.projectStatus.updated
    }

    fun isOwner(project: OutputProject): Boolean {
        return project.applicant.id == securityService.currentUser?.user?.id
    }

    fun isAdmin(): Boolean {
        return securityService.currentUser?.isAdmin!!
    }

    fun isProgrammeUser(): Boolean {
        return securityService.currentUser?.isProgrammeUser!!
    }

}
