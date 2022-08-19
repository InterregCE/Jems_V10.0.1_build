package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportType
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Embedded
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

@Entity(name = "report_project_partner_identification")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectPartnerReportIdentificationEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectPartnerReportIdentificationEntity(

    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    var startDate: LocalDate?,

    var endDate: LocalDate?,

    var periodNumber: Int?,

    @field:NotNull
    var nextReportForecast: BigDecimal,

    @field:NotNull
    var formatOriginals: Boolean,
    @field:NotNull
    var formatCopy: Boolean,
    @field:NotNull
    var formatElectronic: Boolean,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    var type: ReportType,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportIdentificationTranslEntity> = mutableSetOf(),

) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}
