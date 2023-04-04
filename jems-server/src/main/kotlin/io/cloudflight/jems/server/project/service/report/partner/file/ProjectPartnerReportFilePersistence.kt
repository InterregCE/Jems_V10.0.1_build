package io.cloudflight.jems.server.project.service.report.partner.file

import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

interface ProjectPartnerReportFilePersistence {

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
