package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Entity
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

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportIdentificationTranslEntity> = mutableSetOf(),

) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}
