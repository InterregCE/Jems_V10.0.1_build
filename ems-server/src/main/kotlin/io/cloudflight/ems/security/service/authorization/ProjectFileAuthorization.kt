package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.RETURNED_TO_APPLICANT
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

    fun canChangeFile(projectId: Long, fileId: Long): Boolean {
        if (isAdmin())
            return true

        val project = projectService.getById(projectId)
        val status = project.projectStatus.status
        val fileUploaded = fileStorageService.getFileDetail(projectId, fileId).updated
        val lastSubmission = getLastSubmissionFor(project)
        if (isOwner(project))
            return (status == DRAFT || status == RETURNED_TO_APPLICANT) && fileUploaded.isAfter(lastSubmission)

        return false
    }

    /**
     * take last resubmission, if not then submission, if not then it means it is a DRAFT so take status timestamp
     */
    fun getLastSubmissionFor(project: OutputProject): ZonedDateTime {
        return project.resubmissionDate ?: project.submissionDate ?: project.projectStatus.updated
    }

    fun isOwner(project: OutputProject): Boolean {
        return project.applicant.id == securityService.currentUser?.user?.id
    }

    fun isAdmin(): Boolean {
        return securityService.currentUser?.isAdmin!!
    }

}
