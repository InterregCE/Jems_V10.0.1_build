package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@NoRepositoryBean
open class JemsGenericFileService(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
) {
    companion object {
        fun JemsFileCreate.getDefaultMinioFullPath() = "$path$name"
    }

    @Transactional
    open fun persistFileAndPerformAction(
        file: JemsFileCreate,
        additionalStep: (JemsFileMetadataEntity) -> Unit,
    ): JemsFile {
        val bucket = file.type.getBucket()
        val locationForMinio = file.getDefaultMinioFullPath()

        minioStorage.saveFile(
            bucket = bucket,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        return projectFileMetadataRepository.save(
            file.toEntity(
                bucketForMinio = bucket,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            )
        ).also { additionalStep.invoke(it) }.toFullModel()
    }

    @Transactional
    open fun moveFile(fileId: Long, newName: String, newLocation: String) {
        val file = projectFileMetadataRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        minioStorage.moveFile(
            sourceBucket = file.minioBucket,
            sourceFilePath = file.minioLocation,
            destinationBucket = file.minioBucket,
            destinationFilePath = "$newLocation/$newName",
        )
        file.path = newLocation
        file.minioLocation = newLocation
        file.name = newName
    }

    @Transactional
    open fun delete(file: JemsFileMetadataEntity) {
        minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
        projectFileMetadataRepository.delete(file)
    }

    protected fun validateType(type: JemsFileType, allowedFileTypes: Set<JemsFileType>) {
        if (type !in allowedFileTypes) {
            throw WrongFileTypeException(type)
        }
    }

}

class WrongFileTypeException(type: JemsFileType) : ApplicationUnprocessableException(
    code = "GENERIC-FILE-EXCEPTION",
    i18nMessage = I18nMessage("not.allowed.file.type.in.generic.file.repository"),
    message = type.name,
)
