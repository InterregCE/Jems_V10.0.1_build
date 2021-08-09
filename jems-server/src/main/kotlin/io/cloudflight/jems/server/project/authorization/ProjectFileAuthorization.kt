package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.user.service.model.UserRolePermission
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
            fileBelongsToAssessmentCategory(this) && canUpdateAssessmentAttachments()
                ||
                fileBelongsToApplicationCategory(this) && canUpdateApplicationAttachments(projectId)
        }

    fun canRetrieveFileFromCategory(projectId: Long, fileId: Long): Boolean =
        canRetrieveFileFromCategory(projectId, projectFilePersistence.getFileCategoryTypeSet(fileId))

    fun getRetrievableCategories(projectId: Long): Set<ProjectFileCategoryType> =
        mutableSetOf<ProjectFileCategoryType>().also {
            if (canRetrieveAssessmentAttachments())
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
            fileBelongsToAssessmentCategory(this) && canRetrieveAssessmentAttachments() ||
                fileBelongsToApplicationCategory(this) && canRetrieveApplicationAttachments(projectId)
        }

    private fun fileBelongsToApplicationCategory(fileCategories: Set<ProjectFileCategoryType>) =
        containsAny(
            fileCategories,
            ProjectFileCategoryType.APPLICATION,
            ProjectFileCategoryType.INVESTMENT,
            ProjectFileCategoryType.PARTNER
        )

    private fun fileBelongsToAssessmentCategory(fileCategories: Set<ProjectFileCategoryType>) =
        containsAny(fileCategories, ProjectFileCategoryType.ASSESSMENT)


    private fun containsAny(
        fileCategories: Set<ProjectFileCategoryType>, vararg targetCategories: ProjectFileCategoryType
    ): Boolean =
        fileCategories.any { targetCategories.contains(it) }

    private fun canRetrieveAssessmentAttachments() =
        securityService.currentUser?.hasPermission(UserRolePermission.ProjectFileAssessmentRetrieve) ?: false

    private fun canUpdateAssessmentAttachments() =
        securityService.currentUser?.hasPermission(UserRolePermission.ProjectFileAssessmentUpdate) ?: false

    private fun canRetrieveApplicationAttachments(projectId: Long, throwException: Boolean = true) =
        runCatching {
            securityService.currentUser?.hasPermission(UserRolePermission.ProjectFileApplicationRetrieve) ?: false ||
                projectAuthorization.isUserOwnerOfProject(projectId)
        }.onFailure { if (throwException) throw it else Unit }.getOrDefault(false)

    private fun canUpdateApplicationAttachments(projectId: Long) =
        securityService.currentUser?.hasPermission(UserRolePermission.ProjectFileApplicationUpdate) ?: false ||
            projectAuthorization.canUpdateProject(projectId)


}
