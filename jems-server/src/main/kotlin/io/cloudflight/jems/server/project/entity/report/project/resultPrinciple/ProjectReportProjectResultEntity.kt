package io.cloudflight.jems.server.project.entity.report.project.resultPrinciple

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
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

@Entity(name = "report_project_result")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportProjectResultEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportProjectResultEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:NotNull
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "report_id")
    @field:NotNull
    val projectReport: ProjectReportEntity,

    @field:NotNull
    val resultNumber: Int,

    @OneToMany(mappedBy = "translationId.sourceEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    val translatedValues: MutableSet<ProjectReportProjectResultTranslEntity> = mutableSetOf(),

    val periodNumber: Int?,

    @ManyToOne
    @JoinColumn(name = "indicator_result_id")
    val programmeResultIndicatorEntity: ResultIndicatorEntity?,

    @field:NotNull
    val baseline: BigDecimal,

    @field:NotNull
    val targetValue: BigDecimal,

    @field:NotNull
    var currentReport: BigDecimal,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    var attachment: JemsFileMetadataEntity?,

    @field:NotNull
    val deactivated: Boolean,
)
