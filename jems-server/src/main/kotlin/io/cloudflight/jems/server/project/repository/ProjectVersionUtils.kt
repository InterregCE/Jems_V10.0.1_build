package io.cloudflight.jems.server.project.repository

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class ProjectVersionUtils(
    private val projectVersionRepository: ProjectVersionRepository
) {

    @Transactional(readOnly = true)
    fun <T> fetch(
        version: Int?,
        projectId: Long,
        currentVersionFetcher: () -> T,
        previousVersionFetcher: (timestamp: Timestamp) -> T
    ) =
        if (version == null) {
            currentVersionFetcher.invoke()
        } else {
            projectVersionRepository.findTimestampByVersion(projectId, version)?.let {
                previousVersionFetcher.invoke(it)
            } ?: throw ApplicationVersionNotFoundException()
        }
}
