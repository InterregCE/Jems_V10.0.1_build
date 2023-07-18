package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

enum class FileChangeAction {
    Upload,
    Delete,
}

data class ProjectFileChangeEvent(
    val action: FileChangeAction,
    val projectSummary: ProjectSummary,
    val file: JemsFile,
    val overrideAuthorEmail: String? = null
)

@Service
data class ProjectFileNotificationEventListener(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val reportPersistence: ProjectPartnerReportPersistence,
) {

    @EventListener
    fun sendNotifications(event: ProjectFileChangeEvent) {
        val type = event.type()
        if (type != null)
            notificationProjectService.sendNotifications(
                type = type,
                variables = event.projectFileVariables(type),
            )
    }

    private fun ProjectFileChangeEvent.type() = file.type.toNotificationType(action)

    private fun ProjectFileChangeEvent.projectFileVariables(notificationType: NotificationType): Map<NotificationVariable, Any> {
        val variables = mutableMapOf(
            NotificationVariable.ProjectId to projectSummary.id,
            NotificationVariable.ProjectIdentifier to projectSummary.customIdentifier,
            NotificationVariable.ProjectAcronym to projectSummary.acronym,
            NotificationVariable.FileUsername to (overrideAuthorEmail ?: file.author.email),
            NotificationVariable.FileName to file.name,
        )

        val fileType = getPartnerReportFileType(notificationType)
        val reportId = if (fileType != null && file.type.isSubFolderOf(fileType))
            fileType.getItsIdFrom(path = file.indexedPath) else null

        if (reportId != null) {
            with (reportPersistence.getPartnerReportByIdUnsecured(reportId)) {
                variables.putAll(mapOf(
                    NotificationVariable.PartnerId to partnerId,
                    NotificationVariable.PartnerRole to partnerRole,
                    NotificationVariable.PartnerNumber to partnerNumber,
                    NotificationVariable.PartnerAbbreviation to partnerAbbreviation,
                    NotificationVariable.PartnerReportId to id,
                    NotificationVariable.PartnerReportNumber to reportNumber,
                ))
            }
        }

        return variables
    }

    private fun getPartnerReportFileType(type: NotificationType): JemsFileType?  {
        return if (type.isPartnerReportFileActionNotification())
            JemsFileType.PartnerControlReport
        else
            null
    }

}
