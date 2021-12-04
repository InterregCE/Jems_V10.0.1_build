package io.cloudflight.jems.server.project.entity.workpackage.investment

import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
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

    @Embedded
    var address: AddressEntity?,

    var expectedDeliveryPeriod: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "investmentTranslation.investment")
    val translatedValues: MutableSet<WorkPackageInvestmentTransl> = mutableSetOf()
) {

    override fun equals(other: Any?) =
        this === other ||
                other !== null &&
                other is WorkPackageInvestmentEntity &&
                id > 0 &&
                id == other.id

    override fun hashCode() =
        if (id > 0) id.toInt() else super.hashCode()

}