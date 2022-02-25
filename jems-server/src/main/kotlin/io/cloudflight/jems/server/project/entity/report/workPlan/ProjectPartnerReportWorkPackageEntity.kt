package io.cloudflight.jems.server.project.entity.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
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

@Entity(name = "report_project_partner_wp")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectPartnerReportWorkPackageEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectPartnerReportWorkPackageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @field:NotNull
    val number: Int,

    // linked application form entity
    val workPackageId: Long?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportWorkPackageTranslEntity> = mutableSetOf()

)
