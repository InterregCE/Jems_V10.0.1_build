package io.cloudflight.jems.server.notification.inApp.service.model

enum class NotificationVariable(val variable: String) {
    ProgrammeName("programmeName"),

    CallId("callId"),
    CallName("callName"),

    ProjectId("projectId"),
    ProjectIdentifier("projectIdentifier"),
    ProjectAcronym("projectAcronym"),

    PartnerId("partnerId"),
    PartnerRole("partnerRole"),
    PartnerNumber("partnerNumber"),
    PartnerAbbreviation("partnerName"),

    PartnerReportId("partnerReportId"),
    PartnerReportNumber("partnerReportNumber"),

    ProjectReportId("projectReportId"),
    ProjectReportNumber("projectReportNumber"),

    FileUsername("fileUsername"),
    FileName("fileName");

    /**
     * These are the minimum amount of variables that we need
     * in order to send a Notification in-app or through email.
     */
    companion object {
        val projectNotificationVariables = setOf(
            ProjectId,
            ProjectIdentifier,
            ProjectAcronym,
        )

        val partnerReportNotificationVariables = projectNotificationVariables union setOf(
            PartnerId,
            PartnerRole,
            PartnerNumber,
            PartnerAbbreviation,
            PartnerReportId,
            PartnerReportNumber,
        )

        val projectReportNotificationVariables = projectNotificationVariables union setOf(
            ProjectReportId,
            ProjectReportNumber,
        )

        val projectFileNotificationVariables = projectNotificationVariables union setOf(
            FileUsername,
            FileName,
        )

        val partnerReportFileNotificationVariables = partnerReportNotificationVariables union setOf(
            FileUsername,
            FileName,
        )

    }

}
