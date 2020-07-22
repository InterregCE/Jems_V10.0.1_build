package io.cloudflight.ems.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

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
    val programmeAmendingDecisionDate: LocalDate?

)
