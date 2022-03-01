package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
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

@Entity(name = "report_project_partner_identification_tg")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectPartnerReportIdentificationTargetGroupEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectPartnerReportIdentificationTargetGroupEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_identification_id")
    @field:NotNull
    val reportIdentificationEntity: ProjectPartnerReportIdentificationEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectTargetGroup,

    @field:NotNull
    val sortNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportIdentificationTargetGroupTranslEntity> = mutableSetOf(),

)
