package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_call")
class CallEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @field:NotNull
    val creator: UserEntity,

    @Column(unique = true)
    @field:NotNull
    var name: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: CallStatus = CallStatus.DRAFT,

    @field:NotNull
    var startDate: ZonedDateTime,

    var endDateStep1: ZonedDateTime?,

    @field:NotNull
    var endDate: ZonedDateTime,

    @field:NotNull
    var lengthOfPeriod: Int,

    @field:NotNull
    var isAdditionalFundAllowed: Boolean,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<CallTranslEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable(
        name = "project_call_priority_specific_objective",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_specific_objective")]
    )
    val prioritySpecificObjectives: MutableSet<ProgrammeSpecificObjectiveEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable(
        name = "project_call_strategy",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_strategy")]
    )
    val strategies: MutableSet<ProgrammeStrategyEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable(
        name = "project_call_fund",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_fund")]
    )
    val funds: MutableSet<ProgrammeFundEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "setupId.call", cascade = [CascadeType.ALL], orphanRemoval = true)
    val flatRates: MutableSet<ProjectCallFlatRateEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable(
        name = "project_call_lump_sum",
        joinColumns = [JoinColumn(name = "project_call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_lump_sum_id")]
    )
    val lumpSums: MutableSet<ProgrammeLumpSumEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable(
        name = "project_call_unit_cost",
        joinColumns = [JoinColumn(name = "project_call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_unit_cost_id")]
    )
    val unitCosts: MutableSet<ProgrammeUnitCostEntity> = mutableSetOf(),

    @Embedded
    var allowRealCosts: AllowRealCostsEntity = AllowRealCostsEntity()
) {
    fun updateFlatRateSetup(flatRates: Set<ProjectCallFlatRateEntity>) {
        val groupedByType = flatRates.associateBy { it.setupId.type }.toMutableMap()
        // update existing
        this.flatRates.forEach {
            if (groupedByType.keys.contains(it.setupId.type)) {
                val newValue = groupedByType.getValue(it.setupId.type)
                it.rate = newValue.rate
                it.isAdjustable = newValue.isAdjustable
            }
        }
        // remove those that needs to be removed
        this.flatRates.removeIf { !groupedByType.keys.contains(it.setupId.type) }

        // add those that are not yet there
        val existingTypes = this.flatRates.associateBy { it.setupId.type }.keys
        groupedByType.filterKeys { !existingTypes.contains(it) }
            .forEach {
                this.flatRates.add(it.value)
            }
    }
}
