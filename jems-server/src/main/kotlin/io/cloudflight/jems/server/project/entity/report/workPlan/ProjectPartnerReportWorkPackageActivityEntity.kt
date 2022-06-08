package io.cloudflight.jems.server.project.entity.report.workPlan

import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
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

@Entity(name = "report_project_partner_wp_activity")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectPartnerReportWorkPackageActivityEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectPartnerReportWorkPackageActivityEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_work_package_id")
    @field:NotNull
    val workPackageEntity: ProjectPartnerReportWorkPackageEntity,

    @field:NotNull
    val number: Int,

    // linked application form entity
    val activityId: Long?,

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: ReportProjectFileEntity?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportWorkPackageActivityTranslEntity> = mutableSetOf()

)
