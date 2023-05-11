package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import java.math.BigDecimal
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

@Entity(name = "report_project_wp_output")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportWorkPackageOutputEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
            NamedAttributeNode(value = "programmeOutputIndicator"),
            NamedAttributeNode(value = "attachment"),
        ],
    )
)
class ProjectReportWorkPackageOutputEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_work_package_id")
    @field:NotNull
    val workPackageEntity: ProjectReportWorkPackageEntity,

    @field:NotNull
    val number: Int,

    @field:NotNull
    val deactivated: Boolean,

    @ManyToOne
    @JoinColumn(name = "indicator_output_id")
    val programmeOutputIndicator: OutputIndicatorEntity?,

    val periodNumber: Int?,

    @field:NotNull
    val targetValue: BigDecimal,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var currentReport: BigDecimal,

    @field:NotNull
    var previousCurrentReport: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: JemsFileMetadataEntity?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportWorkPackageOutputTranslEntity> = mutableSetOf(),

    )
