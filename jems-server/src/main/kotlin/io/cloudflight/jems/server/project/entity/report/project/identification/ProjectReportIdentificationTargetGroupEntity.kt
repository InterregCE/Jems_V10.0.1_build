package io.cloudflight.jems.server.project.entity.report.project.identification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "report_project_identification_tg")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportIdentificationTargetGroupEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportIdentificationTargetGroupEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_report_id")
    @field:NotNull
    val projectReportEntity: ProjectReportEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectTargetGroup,

    @field:NotNull
    val sortNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportIdentificationTargetGroupTranslEntity> = mutableSetOf(),
)
