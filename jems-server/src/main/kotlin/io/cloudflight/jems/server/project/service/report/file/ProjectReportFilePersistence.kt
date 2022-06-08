package io.cloudflight.jems.server.project.service.report.file

import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportFilePersistence {

    fun existsFile(partnerId: Long, fileId: Long): Boolean

    fun existsFile(location: String, fileName: String): Boolean

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun updatePartnerReportActivityAttachment(activityId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportDeliverableAttachment(deliverableId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportOutputAttachment(outputId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportProcurementAttachment(procurementId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportContributionAttachment(contributionId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportExpenditureAttachment(expenditureId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<ProjectPartnerReportFileType>,
        filterUserIds: Set<Long>,
    ): Page<ProjectReportFile>

    fun addAttachmentToPartnerReport(file: ProjectReportFileCreate): ProjectReportFileMetadata

}
