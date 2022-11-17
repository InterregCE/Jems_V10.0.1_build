package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportFilePersistence {

    fun existsFile(exactPath: String, fileName: String): Boolean

    fun existsFile(partnerId: Long, pathPrefix: String, fileId: Long): Boolean

    fun existsFile(type: JemsFileType, fileId: Long): Boolean

    fun existsFileByProjectIdAndFileIdAndFileTypeIn(
        projectId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean

    fun existsFileByPartnerIdAndFileIdAndFileTypeIn(partnerId: Long, fileId: Long, fileTypes: Set<JemsFileType>): Boolean

    fun getFileAuthor(partnerId: Long, pathPrefix: String, fileId: Long): UserSimple?

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadFile(type: JemsFileType, fileId: Long): Pair<String, ByteArray>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun deleteFile(type: JemsFileType, fileId: Long)

    fun setDescriptionToFile(fileId: Long, description: String)

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

    fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<JemsFileType>,
        filterUserIds: Set<Long>,
    ): Page<JemsFile>

    fun addAttachmentToPartnerReport(file: JemsFileCreate): JemsFileMetadata

    fun getFileType(fileId: Long, projectId: Long): JemsFileType?

    fun getFileTypeByPartnerId(fileId: Long, partnerId: Long): JemsFileType

}
