package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.InputStream

interface JemsFilePersistence {

    fun existsFile(exactPath: String, fileName: String): Boolean

    fun fileIdIfExists(exactPath: String, fileName: String): Long?

    fun existsFile(exactPath: String, fileId: Long): Boolean

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

    fun getFile(partnerId: Long, pathPrefix: String, fileId: Long): JemsFile?

    fun getFile(projectId: Long, fileId: Long): JemsFile?

    fun downloadFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadReportFile(projectId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadFile(type: JemsFileType, fileId: Long): Pair<String, ByteArray>?

    fun downloadFileAsStream(type: JemsFileType, fileId: Long): Pair<String, InputStream>?

    fun deleteFile(partnerId: Long, fileId: Long)

    fun deleteReportFile(projectId: Long, fileId: Long)

    fun deleteFile(type: JemsFileType, fileId: Long)

    fun setDescriptionToFile(fileId: Long, description: String)

    fun getFileType(fileId: Long, projectId: Long): JemsFileType?

    fun getFileTypeByPartnerId(fileId: Long, partnerId: Long): JemsFileType
}
