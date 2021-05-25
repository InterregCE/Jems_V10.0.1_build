package io.cloudflight.jems.server.project.repository

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class ProjectVersionUtils(
    private val projectVersionRepository: ProjectVersionRepository
) {

    companion object {
        const val DEFAULT_VERSION = "1.0"
        fun increaseMajor(version: String?) = (version?.toFloatOrNull()?.plus(1) ?: 1.0).toString()
    }

    @Transactional(readOnly = true)
    fun <T> fetch(
        version: String?,
        projectId: Long,
        currentVersionFetcher: () -> T,
        previousVersionFetcher: (timestamp: Timestamp) -> T
    ) =
        if (version == null) {
            currentVersionFetcher.invoke()
        } else {
            projectVersionRepository.findTimestampByVersion(projectId, version)?.let {
                previousVersionFetcher.invoke(it)
            }
        }
}
