package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.AddressEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_investment")
data class WorkPackageInvestmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_package_id")
    @field:NotNull
    var workPackage: WorkPackageEntity,

    @Column
    var investmentNumber: Int,

    @Column
    var title: String? = null,

    @Column
    var justificationExplanation: String? = null,

    @Column
    var justificationTransactionalRelevance: String? = null,

    @Column
    var justificationBenefits: String? = null,

    @Column
    var justificationPilot: String? = null,

    @Embedded
    var address: AddressEntity?,

    @Column
    var risk: String? = null,

    @Column
    var documentation: Int? = null,

    @Column
    var ownershipSiteLocation: String? = null,

    @Column
    var ownershipRetain: String? = null,

    @Column
    var ownershipMaintenance: String? = null
)
