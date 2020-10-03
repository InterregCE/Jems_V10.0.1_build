package io.cloudflight.jems.server.entity

import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.OneToMany

@Entity(name = "programme_data")
data class ProgrammeData(

    @Id
    val id: Long = 1,

    @Column
    val cci: String?,

    @Column
    val title: String?,

    @Column
    val version: String?,

    @Column
    val firstYear: Int?,

    @Column
    val lastYear: Int?,

    @Column
    val eligibleFrom: LocalDate?,

    @Column
    val eligibleUntil: LocalDate?,

    @Column
    val commissionDecisionNumber: String?,

    @Column
    val commissionDecisionDate: LocalDate?,

    @Column
    val programmeAmendingDecisionNumber: String?,

    @Column
    val programmeAmendingDecisionDate: LocalDate?,

    @Column
    val languagesSystem: String?,

    @OneToMany
    @JoinTable(
        name = "programme_nuts",
        joinColumns = [JoinColumn(name = "programme_data_id")],
        inverseJoinColumns = [JoinColumn(name = "nuts_region_3_id")]
    )
    val programmeNuts: Set<NutsRegion3> = emptySet()

)
