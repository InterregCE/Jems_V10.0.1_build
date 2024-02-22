package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.resetTranslations
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "report_project_closure_project_prize")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportProjectClosurePrizeEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportProjectClosurePrizeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    @field:NotNull
    var sortNumber: Int,

    @Column
    @field:NotNull
    val reportId: Long,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    var translatedValues: MutableSet<ProjectReportProjectClosurePrizeTranslEntity> = mutableSetOf()
) {

    fun updateTranslations(newTranslations: Set<ProjectReportProjectClosurePrizeTranslEntity>) {
        this.translatedValues.resetTranslations(newTranslations) { currentTranslation, newTranslation ->
            currentTranslation.prize = newTranslation.prize
        }
    }
}
