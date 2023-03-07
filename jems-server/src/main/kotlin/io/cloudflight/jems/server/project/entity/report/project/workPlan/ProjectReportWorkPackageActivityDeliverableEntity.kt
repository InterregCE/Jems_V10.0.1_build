package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import java.math.BigDecimal
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

@Entity(name = "report_project_wp_activity_deliverable")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportWorkPackageActivityDeliverableEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
            NamedAttributeNode(value = "attachment"),
        ],
    )
)
class ProjectReportWorkPackageActivityDeliverableEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_activity_id")
    @field:NotNull
    val activityEntity: ProjectReportWorkPackageActivityEntity,

    @field:NotNull
    val number: Int,

    @field:NotNull
    val deactivated: Boolean,

    // linked application form entity
    val deliverableId: Long?,

    val periodNumber: Int?,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var currentReport: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: JemsFileMetadataEntity?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportWorkPackageActivityDeliverableTranslEntity> = mutableSetOf()

)
