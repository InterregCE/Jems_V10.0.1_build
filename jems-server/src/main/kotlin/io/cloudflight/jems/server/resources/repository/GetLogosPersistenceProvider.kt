package io.cloudflight.jems.server.resources.repository

import io.cloudflight.jems.api.common.dto.LogoDTO
import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.server.common.minio.MinioStorage
import org.springframework.stereotype.Repository
import java.util.Base64

private const val LOGO_FILE_BUCKET_NAME = "jems-logo-file-bucket"

@Repository
class GetLogosPersistenceProvider(
    private val minioStorage: MinioStorage,
): GetLogosPersistence {
    override fun getLogos(): List<LogoDTO> {
        return LogoType.values()
            .filter {
                minioStorage.exists(LOGO_FILE_BUCKET_NAME, it.key)
            }
            .map {
                val encodedFile = Base64.getEncoder().encodeToString(minioStorage.getFile(LOGO_FILE_BUCKET_NAME, it.key))
                LogoDTO(it, encodedFile)
            }
    }
}
