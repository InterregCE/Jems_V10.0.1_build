package io.cloudflight.jems.server.project.entity.report.project.resultPrinciple

import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_horizontal_principle")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportHorizontalPrincipleEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportHorizontalPrincipleEntity(

    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val projectReport: ProjectReportEntity,

    @Enumerated(EnumType.STRING)
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect?,

    @Enumerated(EnumType.STRING)
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect?,

    @Enumerated(EnumType.STRING)
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportHorizontalPrincipleTranslEntity> = mutableSetOf()
)
