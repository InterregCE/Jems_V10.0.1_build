package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryTranslEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportProjectClosureStoryRepository: JpaRepository<ProjectReportProjectClosureStoryTranslEntity, TranslationId<ProjectReportEntity>> {

    fun findAllByTranslationIdSourceEntity(sourceEntity: ProjectReportEntity): MutableSet<ProjectReportProjectClosureStoryTranslEntity>

    fun deleteAllByTranslationIdSourceEntity(sourceEntity: ProjectReportEntity)

}
