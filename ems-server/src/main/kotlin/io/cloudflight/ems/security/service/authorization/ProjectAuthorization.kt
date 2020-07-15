package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.FileStorageService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService,
    val fileStorageService: FileStorageService
) {

    fun canReadProject(id: Long): Boolean {
        if (securityService.currentUser?.hasRole(ADMINISTRATOR)!!) {
            return true;
        }
        val project = projectService.getById(id);
        if (securityService.currentUser?.hasRole(APPLICANT_USER)!!) {
            return securityService.currentUser?.user?.id == project.applicant.id
        }
        // programme user can only read submitted and returned to applicant
        return project.projectStatus.status == ProjectApplicationStatus.SUBMITTED
                || project.projectStatus.status == ProjectApplicationStatus.RETURNED_TO_APPLICANT
    }

    fun canWriteProject(id: Long): Boolean {
        val project = projectService.getById(id)
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
                || (project.applicant.id == securityService.currentUser?.user?.id
                && (project.projectStatus.status == ProjectApplicationStatus.DRAFT
                || project.projectStatus.status == ProjectApplicationStatus.RETURNED_TO_APPLICANT))
    }

    fun canCreateProject(): Boolean {
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
                || securityService.currentUser?.hasRole(APPLICANT_USER)!!
    }

    fun canReadWriteProject(id: Long): Boolean {
        return canReadProject(id) && canWriteProject(id);
    }

    fun canDeleteFile(projectId: Long, fileId: Long): Boolean {
        val project = projectService.getById(projectId)
        val fileUploaded = fileStorageService.getFileDetail(projectId, fileId).updated
        // take last resubmission, if not then submission, if not then it means it is a DRAFT so take status timestamp
        val lastSubmission = project.resubmissionDate ?: project.submissionDate ?: project.projectStatus.updated

        return canReadWriteProject(projectId) && fileUploaded.isAfter(lastSubmission)
    }
}
