package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanInvestmentStatus
import javax.persistence.CascadeType
import javax.persistence.Embedded
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

@Entity(name = "report_project_wp_investment")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportWorkPackageInvestmentEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportWorkPackageInvestmentEntity(

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

    @Embedded
    var address: AddressEntity?,

    val expectedDeliveryPeriod: Int?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportWorkPackageInvestmentTranslEntity> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    var status: ProjectReportWorkPlanInvestmentStatus?,

    @Enumerated(EnumType.STRING)
    var previousStatus: ProjectReportWorkPlanInvestmentStatus?,
)
