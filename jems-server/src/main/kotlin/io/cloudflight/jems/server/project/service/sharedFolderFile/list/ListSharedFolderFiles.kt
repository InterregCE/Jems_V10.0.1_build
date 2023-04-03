package io.cloudflight.jems.server.project.service.sharedFolderFile.list

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveSharedFolder
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListSharedFolderFiles(
    private val filePersistence: JemsFilePersistence
) : ListSharedFolderFilesInteractor {

    @CanRetrieveSharedFolder
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListSharedFolderFilesException::class)
    override fun list(projectId: Long, pageable: Pageable): Page<JemsFile> =
        filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = JemsFileType.SharedFolder.generatePath(projectId),
            filterSubtypes = emptySet(),
            filterUserIds = emptySet()
        )

}
