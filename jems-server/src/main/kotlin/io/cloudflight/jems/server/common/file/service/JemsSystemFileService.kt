package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JemsSystemFileService(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
) : JemsGenericFileService(projectFileMetadataRepository, minioStorage, userRepository) {

    companion object {
        private val allowedFileTypes = setOf(
            // Call-Specific translation file
            JemsFileType.CallTranslation,
        )
    }

    @Transactional
    fun persistFile(file: JemsFileCreate) =
        this.persistFileAndPerformAction(file) { /* do nothing */ }

    @Transactional
    override fun persistFileAndPerformAction(
        file: JemsFileCreate,
        additionalStep: (JemsFileMetadataEntity) -> Unit,
    ): JemsFile {
        validateType(file.type, allowedFileTypes)
        return super.persistFileAndPerformAction(file, additionalStep)
    }

    @Transactional
    override fun delete(file: JemsFileMetadataEntity) {
        validateType(file.type, allowedFileTypes)
        super.delete(file)
    }

}
