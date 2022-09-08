package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileApplicationRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileApplicationUpdate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileAssessmentUpdate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationFileAssessmentUpdate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectFileAuthorization.canUploadFileInCategory(#projectId, #projectFileCategory)")
annotation class CanUploadFileInCategory

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectFileAuthorization.canUpdateFileInCategory(#projectId, #fileId)")
annotation class CanUpdateFileDescriptionInCategory

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectFileAuthorization.canUpdateFileInCategory(#projectId, #fileId)")
annotation class CanDeleteFileInCategory

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectFileAuthorization.canRetrieveFileFromCategory(#projectId, #fileId)")
annotation class CanDownloadFileFromCategory

@Component
class ProjectFileAuthorization(
    override val securityService: SecurityService,
    val projectAuthorization: ProjectAuthorization,
    val projectFilePersistence: ProjectFilePersistence
) : Authorization(securityService) {

    fun canUploadFileInCategory(projectId: Long, projectFileCategory: ProjectFileCategory): Boolean =
        canUpdateFileInCategory(projectId, setOf(projectFileCategory.type))

    fun canUpdateFileInCategory(projectId: Long, fileId: Long): Boolean =
        canUpdateFileInCategory(projectId, projectFilePersistence.getFileCategoryTypeSet(fileId))

    private fun canUpdateFileInCategory(projectId: Long, fileCategoryTypeSet: Set<ProjectFileCategoryType>) =
        with(fileCategoryTypeSet) {
            fileBelongsToModificationCategory(this) && canUpdateModificationAttachments(projectId)
                || fileBelongsToAssessmentCategory(this) && canUpdateAssessmentAttachments(projectId = projectId)
                || fileBelongsToApplicationCategory(this) && canUpdateApplicationAttachments(projectId)
        }

    fun canRetrieveFileFromCategory(projectId: Long, fileId: Long): Boolean =
        canRetrieveFileFromCategory(projectId, projectFilePersistence.getFileCategoryTypeSet(fileId))

    fun getRetrievableCategories(projectId: Long): Set<ProjectFileCategoryType> =
        mutableSetOf<ProjectFileCategoryType>().also {
            if (canRetrieveModificationAttachments(projectId = projectId))
                it.add(ProjectFileCategoryType.MODIFICATION)
            if (canRetrieveAssessmentAttachments(projectId = projectId))
                it.add(ProjectFileCategoryType.ASSESSMENT)
            if (canRetrieveApplicationAttachments(projectId, false))
                it.addAll(
                    listOf(
                        ProjectFileCategoryType.APPLICATION,
                        ProjectFileCategoryType.PARTNER,
                        ProjectFileCategoryType.INVESTMENT
                    )
                )
        }

    private fun canRetrieveFileFromCategory(
        projectId: Long,
        fileCategoryTypeSet: Set<ProjectFileCategoryType>
    ): Boolean =
        with(fileCategoryTypeSet) {
            fileBelongsToModificationCategory(this) && canRetrieveModificationAttachments(projectId) ||
            fileBelongsToAssessmentCategory(this) && canRetrieveAssessmentAttachments(projectId = projectId) ||
                fileBelongsToApplicationCategory(this) && canRetrieveApplicationAttachments(projectId)
        }

    private fun fileBelongsToApplicationCategory(fileCategories: Set<ProjectFileCategoryType>) =
        containsAny(
            fileCategories,
            ProjectFileCategoryType.APPLICATION,
            ProjectFileCategoryType.INVESTMENT,
            ProjectFileCategoryType.PARTNER
        )

    private fun fileBelongsToModificationCategory(fileCategories: Set<ProjectFileCategoryType>)
        = containsAny(fileCategories, ProjectFileCategoryType.MODIFICATION)

    private fun fileBelongsToAssessmentCategory(fileCategories: Set<ProjectFileCategoryType>) =
        containsAny(fileCategories, ProjectFileCategoryType.ASSESSMENT)


    private fun containsAny(
        fileCategories: Set<ProjectFileCategoryType>, vararg targetCategories: ProjectFileCategoryType
    ): Boolean =
        fileCategories.any { targetCategories.contains(it) }

    private fun canRetrieveAssessmentAttachments(projectId: Long) =
        hasPermissionForProject(ProjectFileAssessmentRetrieve, projectId)

    private fun canUpdateAssessmentAttachments(projectId: Long) =
        hasPermissionForProject(ProjectFileAssessmentUpdate, projectId)

    private fun canRetrieveModificationAttachments(projectId: Long) =
        hasPermissionForProject(ProjectModificationFileAssessmentRetrieve, projectId)

    private fun canUpdateModificationAttachments(projectId: Long) =
        hasPermissionForProject(ProjectModificationFileAssessmentUpdate, projectId)

    private fun canRetrieveApplicationAttachments(projectId: Long, throwException: Boolean = true) =
        runCatching {
            hasPermissionForProject(ProjectFileApplicationRetrieve, projectId) ||
                projectAuthorization.isUserViewCollaboratorForProjectOrThrow(projectId)
        }.onFailure { if (throwException) throw it else Unit }.getOrDefault(false)

    private fun canUpdateApplicationAttachments(projectId: Long) =
        hasPermissionForProject(ProjectFileApplicationUpdate, projectId) ||
            projectAuthorization.canUpdateProject(projectId, onlyBeforeApproved = false)


}
