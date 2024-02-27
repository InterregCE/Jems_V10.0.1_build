package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosureStoryTranslEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportClosureStoryRepository: JpaRepository<ProjectReportClosureStoryTranslEntity, TranslationId<ProjectReportEntity>> {

    fun findAllByTranslationIdSourceEntity(sourceEntity: ProjectReportEntity): MutableSet<ProjectReportClosureStoryTranslEntity>

    fun deleteAllByTranslationIdSourceEntity(sourceEntity: ProjectReportEntity)

}
