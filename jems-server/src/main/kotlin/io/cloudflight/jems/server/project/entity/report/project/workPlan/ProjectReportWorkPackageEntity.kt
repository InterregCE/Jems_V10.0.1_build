package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportWorkPackageEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportWorkPackageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectReportEntity,

    @field:NotNull
    val number: Int,

    @field:NotNull
    val deactivated: Boolean,

    // linked application form entity
    val workPackageId: Long?,

    @Enumerated(EnumType.STRING)
    var specificStatus: ProjectReportWorkPlanStatus?,

    @Enumerated(EnumType.STRING)
    var communicationStatus: ProjectReportWorkPlanStatus?,

    @field:NotNull
    var completed: Boolean,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportWorkPackageTranslEntity> = mutableSetOf()

)
