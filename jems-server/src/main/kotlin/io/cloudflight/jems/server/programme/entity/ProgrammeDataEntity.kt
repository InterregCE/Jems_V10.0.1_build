package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import java.time.LocalDate
import java.util.Collections.emptySet
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_data")
data class ProgrammeDataEntity(

    @Id
    val id: Long = 1,

    val cci: String?,
    val title: String?,
    val version: String?,

    val firstYear: Int?,
    val lastYear: Int?,

    val eligibleFrom: LocalDate?,
    val eligibleUntil: LocalDate?,

    val commissionDecisionNumber: String?,
    val commissionDecisionDate: LocalDate?,

    val programmeAmendingDecisionNumber: String?,
    val programmeAmendingDecisionDate: LocalDate?,

    val projectIdProgrammeAbbreviation: String?,
    @field:NotNull
    val projectIdUseCallId: Boolean,

    val defaultUserRoleId: Long? = null,

    @OneToMany
    @JoinTable(
        name = "programme_nuts",
        joinColumns = [JoinColumn(name = "programme_data_id")],
        inverseJoinColumns = [JoinColumn(name = "nuts_region_3_id")]
    )
    val programmeNuts: Set<NutsRegion3> = emptySet(),

)
