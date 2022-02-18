package io.cloudflight.jems.server.project.entity.report.workPlan

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

@Entity(name = "report_project_partner_wp_output")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectPartnerReportWorkPackageOutputEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectPartnerReportWorkPackageOutputEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_work_package_id")
    @field:NotNull
    val workPackageEntity: ProjectPartnerReportWorkPackageEntity,

    @field:NotNull
    val number: Int,

    var contribution: Boolean?,

    var evidence: Boolean?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportWorkPackageOutputTranslEntity> = mutableSetOf()

)
