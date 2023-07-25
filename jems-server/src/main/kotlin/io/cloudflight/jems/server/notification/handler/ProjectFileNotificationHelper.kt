package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

fun FileChangeAction.toNotificationType(fileType: JemsFileType): NotificationType? = when (this) {
    FileChangeAction.Upload -> fileType.toUploadNotification()
    FileChangeAction.Delete -> fileType.toDeleteNotification()
}.enforceIsProjectFileNotification()

private fun JemsFileType.toUploadNotification(): NotificationType? = when {
    isSubFolderOf(JemsFileType.PartnerControlReport) -> NotificationType.ControlCommunicationFileUpload
    this == JemsFileType.SharedFolder -> NotificationType.SharedFolderFileUpload
    else -> null
}

private fun JemsFileType.toDeleteNotification(): NotificationType? = when {
    isSubFolderOf(JemsFileType.PartnerControlReport) -> NotificationType.ControlCommunicationFileDelete
    this == JemsFileType.SharedFolder -> NotificationType.SharedFolderFileDelete
    else -> null
}

fun NotificationType?.enforceIsProjectFileNotification() =
    if (this != null && (isProjectFileNotification() || isPartnerReportFileNotification())) this else null
