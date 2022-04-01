package io.cloudflight.jems.server.project.service.report.file

import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface ProjectReportFilePersistence {

    fun existsFile(partnerId: Long, fileId: Long): Boolean

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun updatePartnerReportActivityAttachment(activityId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportDeliverableAttachment(deliverableId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportOutputAttachment(outputId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportProcurementAttachment(procurementId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

}
