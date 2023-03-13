package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface JemsFilePersistence {

    fun existsFile(exactPath: String, fileName: String): Boolean

    fun existsFile(partnerId: Long, pathPrefix: String, fileId: Long): Boolean

    fun existsReportFile(projectId: Long, pathPrefix: String, fileId: Long): Boolean

    fun existsFile(type: JemsFileType, fileId: Long): Boolean

    fun existsFileByProjectIdAndFileIdAndFileTypeIn(
        projectId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean

    fun existsFileByPartnerIdAndFileIdAndFileTypeIn(
        partnerId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean

    fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<JemsFileType>,
        filterUserIds: Set<Long>,
    ): Page<JemsFile>

    fun getFileAuthor(partnerId: Long, pathPrefix: String, fileId: Long): UserSimple?

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadReportFile(projectId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadFile(type: JemsFileType, fileId: Long): Pair<String, ByteArray>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun deleteReportFile(projectId: Long, fileId: Long)

    fun deleteFile(type: JemsFileType, fileId: Long)

    fun setDescriptionToFile(fileId: Long, description: String)

    fun getFileType(fileId: Long, projectId: Long): JemsFileType?

    fun getFileTypeByPartnerId(fileId: Long, partnerId: Long): JemsFileType
}
