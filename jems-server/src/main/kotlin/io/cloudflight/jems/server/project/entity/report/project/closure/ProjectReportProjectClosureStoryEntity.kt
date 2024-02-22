package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.resetTranslations
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity(name = "report_project_closure_project_story")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportProjectClosureStoryEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportProjectClosureStoryEntity(

    @Id
    @Column(name = "report_id")
    val reportId: Long = 0,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    var translatedValues: MutableSet<ProjectReportProjectClosureStoryTranslEntity> = mutableSetOf()

) {
    fun updateTranslations(newTranslations: Set<ProjectReportProjectClosureStoryTranslEntity>) {
        this.translatedValues.resetTranslations(newTranslations) { currentTranslation, newTranslation ->
            currentTranslation.story = newTranslation.story
        }
    }
}
