package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface ProjectReportFilePersistence {

    fun updatePartnerReportActivityAttachment(
        activityId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun updatePartnerReportDeliverableAttachment(
        deliverableId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun updatePartnerReportOutputAttachment(outputId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updatePartnerReportContributionAttachment(
        contributionId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun updatePartnerReportExpenditureAttachment(
        expenditureId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun addPartnerReportProcurementAttachment(
        reportId: Long,
        procurementId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun addAttachmentToPartnerReport(file: JemsFileCreate): JemsFileMetadata

}
