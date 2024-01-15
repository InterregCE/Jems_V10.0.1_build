package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
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
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence
) {

    @EventListener
    fun sendNotifications(event: ProjectFileChangeEvent) {
        val type = event.type()
        if (type != null)
            notificationProjectService.sendNotifications(type = type, variables = event.projectFileVariables())
    }

    private fun ProjectFileChangeEvent.type() = action.toNotificationType(file.type)

    private fun ProjectFileChangeEvent.projectFileVariables(): Map<NotificationVariable, Any> {
        val variables = mutableMapOf(
            NotificationVariable.ProjectId to projectSummary.id,
            NotificationVariable.ProjectIdentifier to projectSummary.customIdentifier,
            NotificationVariable.ProjectAcronym to projectSummary.acronym,
            NotificationVariable.FileUsername to (overrideAuthorEmail ?: file.author.email),
            NotificationVariable.FileName to file.name,
        )

        val partnerReportParentNode = file.type.getPartnerReportParentNodeIfPresent()
        val reportId = partnerReportParentNode?.getItsIdFrom(path = file.indexedPath)

        if (reportId != null) {
            with (reportPersistence.getPartnerReportByIdUnsecured(reportId)) {
                val periods = reportIdentificationPersistence.getAvailablePeriods(partnerId, id)
                val period = periods.firstOrNull { it.number == periodNumber }

                variables.putAll(mapOf(
                    NotificationVariable.PartnerId to partnerId,
                    NotificationVariable.PartnerRole to partnerRole,
                    NotificationVariable.PartnerNumber to partnerNumber,
                    NotificationVariable.PartnerAbbreviation to partnerAbbreviation,
                    NotificationVariable.PartnerReportId to id,
                    NotificationVariable.PartnerReportNumber to reportNumber,
                    NotificationVariable.ReportingPeriodNumber to (period?.number ?: ""),
                    NotificationVariable.ReportingPeriodStart to (period?.start ?: ""),
                    NotificationVariable.ReportingPeriodEnd to (period?.end ?: "")
                ))
            }
        }

        val projectReportParentNode = file.type.getProjectReportParentNodeIfPresent()
        val projectReportId = projectReportParentNode?.getItsIdFrom(path = file.indexedPath)

        if(projectReportId != null) {
           with (projectReportPersistence.getReportByIdUnSecured(projectReportId)) {
               val periods = projectPersistence.getProjectPeriods(projectSummary.id, linkedFormVersion)
               val period = periods.firstOrNull { it.number == periodNumber }

               variables.putAll(mapOf(
                   NotificationVariable.ProjectReportId to id,
                   NotificationVariable.ProjectReportNumber to reportNumber,
                   NotificationVariable.ReportingPeriodNumber to (period?.number ?: ""),
                   NotificationVariable.ReportingPeriodStart to (period?.start ?: ""),
                   NotificationVariable.ReportingPeriodEnd to (period?.end ?: "")
               ))
           }
        }

        return variables
    }

    private fun JemsFileType.getPartnerReportParentNodeIfPresent(): JemsFileType? =
        when {
            isSubFolderOf(JemsFileType.PartnerReport) -> JemsFileType.PartnerReport
            isSubFolderOf(JemsFileType.PartnerControlReport) -> JemsFileType.PartnerControlReport
            else -> null
        }

    private fun JemsFileType.getProjectReportParentNodeIfPresent(): JemsFileType? =
        if (isSubFolderOf(JemsFileType.ProjectReport)) {
            JemsFileType.ProjectReport
        } else {
            null
        }
}
