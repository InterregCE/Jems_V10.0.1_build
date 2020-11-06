package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.entity.Strategy
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_call")
data class Call(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    @field:NotNull
    val creator: User,

    @Column(unique = true)
    @field:NotNull
    val name: String,

    @OneToMany
    @JoinTable(
        name = "project_call_priority_policy",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_priority_policy")]
    )
    val priorityPolicies: Set<ProgrammePriorityPolicy>,

    @OneToMany
    @JoinTable(
        name = "project_call_strategy",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_strategy")]
    )
    val strategies: Set<Strategy>,

    @OneToMany
    @JoinTable(
        name = "project_call_fund",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "programme_fund")]
    )
    val funds: Set<ProgrammeFund>,

    @field:NotNull
    val startDate: ZonedDateTime,

    @field:NotNull
    val endDate: ZonedDateTime,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val status: CallStatus,

    @field:NotNull
    val lengthOfPeriod: Int,

    val description: String? = null,

    @OneToMany(mappedBy = "setupId.callId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var flatRateSetup: MutableSet<FlatRateSetup> = mutableSetOf()

) {
    fun updateFlatRateSetup(flatRates: Set<FlatRateSetup>) {
        val groupedByType = flatRates.associateBy { it.setupId.type }.toMutableMap()
        // update existing
        flatRateSetup.forEach {
            if (groupedByType.keys.contains(it.setupId.type)) {
                val newValue = groupedByType.getValue(it.setupId.type)
                it.rate = newValue.rate
                it.isAdjustable = newValue.isAdjustable
            }
        }
        // remove those that needs to be removed
        flatRateSetup.removeIf { !groupedByType.keys.contains(it.setupId.type) }

        // add those that are not yet there
        val existingTypes = flatRateSetup.associateBy { it.setupId.type }.keys
        groupedByType.filterKeys { !existingTypes.contains(it) }
            .forEach {
                flatRateSetup.add(it.value)
            }
    }
}
