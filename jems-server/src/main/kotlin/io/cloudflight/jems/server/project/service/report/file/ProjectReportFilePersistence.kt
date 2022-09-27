package io.cloudflight.jems.server.project.service.report.file

import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportFilePersistence {

    fun existsFile(exactPath: String, fileName: String): Boolean

    fun existsFile(partnerId: Long, pathPrefix: String, fileId: Long): Boolean

    fun existsFileByProjectIdAndFileIdAndFileTypeIn(projectId: Long, fileId: Long, fileTypes: Set<ProjectPartnerReportFileType>): Boolean

    fun getFileAuthor(partnerId: Long, pathPrefix: String, fileId: Long): UserSimple?

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun setDescriptionToFile(fileId: Long, description: String)

    fun updatePartnerReportActivityAttachment(activityId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportDeliverableAttachment(deliverableId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportOutputAttachment(outputId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportContributionAttachment(contributionId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun updatePartnerReportExpenditureAttachment(expenditureId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun addPartnerReportProcurementAttachment(reportId: Long, procurementId: Long, file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<ProjectPartnerReportFileType>,
        filterUserIds: Set<Long>,
    ): Page<ProjectReportFile>

    fun addAttachmentToPartnerReport(file: ProjectReportFileCreate): ProjectReportFileMetadata

}
