package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

fun JemsFileType.toNotificationType(action: FileChangeAction): NotificationType? = when {
    this == JemsFileType.ControlDocument && action == FileChangeAction.Upload -> NotificationType.ControlCommunicationFileUpload
    this == JemsFileType.ControlCertificate && action == FileChangeAction.Upload -> NotificationType.ControlCommunicationFileUpload
    this == JemsFileType.ControlReport && action == FileChangeAction.Upload -> NotificationType.ControlCommunicationFileUpload

    this == JemsFileType.ControlDocument && action == FileChangeAction.Delete -> NotificationType.ControlCommunicationFileDelete
    this == JemsFileType.ControlCertificate && action == FileChangeAction.Delete -> NotificationType.ControlCommunicationFileDelete
    this == JemsFileType.ControlReport && action == FileChangeAction.Delete -> NotificationType.ControlCommunicationFileDelete

    this == JemsFileType.SharedFolder && action == FileChangeAction.Upload -> NotificationType.SharedFolderFileUpload
    this == JemsFileType.SharedFolder && action == FileChangeAction.Delete -> NotificationType.SharedFolderFileDelete
    else -> null
}.enforceIsProjectFileNotification()

fun NotificationType?.enforceIsProjectFileNotification() =
    if (this != null && (isProjectFileActionNotification() || isPartnerReportFileActionNotification())) this else null


